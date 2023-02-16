package com.mentoree.service;

import com.mentoree.config.utils.files.FileUtils;
import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.*;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.BoardCreateRequestDto;
import com.mentoree.service.dto.BoardInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final MemberRepository memberRepository;
    private final MissionRepository missionRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final AttachImageRepository attachImageRepository;
    private final FileUtils fileUtils;

    @Transactional
    public Long create(Long memberId, BoardCreateRequestDto request, boolean temporal) {

        Optional<Board> temporalBoard = boardRepository.findTemporalBoard(memberId, request.getMissionId());

        if(temporalBoard.isEmpty()) {
            Member writer = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
            Mission mission = missionRepository.findById(request.getMissionId())
                    .orElseThrow(NoDataFoundException::new);
            Board newBoard = request.toEntity(mission, writer, temporal);
            Board saved = boardRepository.save(newBoard);
            return saved.getId();
        }

        Writing updatedWriting = new Writing(request.getTitle(), request.getDescription());
        Board tempBoard = temporalBoard.get();
        tempBoard.update(updatedWriting);
        if(!temporal) {
            tempBoard.save();
            cleanUpFiles(tempBoard);
        }
        return tempBoard.getId();
    }

    @Transactional
    public void update(Long boardId, BoardCreateRequestDto updateRequest) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(NoDataFoundException::new);
        Writing updateWriting = new Writing(updateRequest.getTitle(), updateRequest.getDescription());
        findBoard.update(updateWriting);
        cleanUpFiles(findBoard);
    }

    @Transactional
    public void delete(Long boardId) {
        List<Reply> replyList = replyRepository.findAllByBoardId(boardId);
        for (Reply reply : replyList) {
            reply.deleteBoard();
        }
        boardRepository.deleteById(boardId);
    }

    @Transactional(readOnly = true)
    public BoardInfoDto getBoardInfo(Long boardId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(NoDataFoundException::new);
        return BoardInfoDto.of(findBoard);
    }

    @Transactional(readOnly = true)
    public List<BoardInfoDto> getBoardInfoList(Long missionId) {
        List<Board> findResult = boardRepository.findAllByMissionId(missionId);
        return findResult.stream().map(BoardInfoDto::of).collect(Collectors.toList());
    }

    @Transactional
    public String uploadImages(Long boardId, MultipartFile file) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(NoDataFoundException::new);

        String originFilename = file.getOriginalFilename();
        String saveFilename = fileUtils.getSaveFilename(originFilename);
        String path = fileUtils.uploadFile(file, saveFilename);

        ImageFile imageFile = ImageFile.builder()
                .filePath(path)
                .originFileName(originFilename)
                .saveFileName(saveFilename)
                .mediaType(FilenameUtils.getExtension(originFilename))
                .build();

        AttachImage attachImage = AttachImage.builder()
                .image(imageFile)
                .build();

        attachImage.setBoard(findBoard);
        attachImageRepository.save(attachImage);

        return path;
    }

    @Transactional(readOnly = true)
    public Optional<BoardInfoDto> getTemporalWriting(Long memberId, Long missionId) {
        Optional<Board> temporalBoard = boardRepository.findTemporalBoard(memberId, missionId);
        if(temporalBoard.isEmpty())
            return Optional.ofNullable(null);
        else
            return Optional.ofNullable(BoardInfoDto.of(temporalBoard.get()));
    }

    private void cleanUpFiles(Board board) {
        Map<String, List<AttachImage>> separateResult = separateImageList(board);
        List<AttachImage> confirmImage = separateResult.get("confirm");
        List<AttachImage> removeImage = separateResult.get("remove");

        board.cleanUpImage(confirmImage);
        cleanUpGarbageFiles(removeImage);
    }

    private void cleanUpGarbageFiles(List<AttachImage> removeImage) {
        for (AttachImage img : removeImage) {
            fileUtils.deleteFile(img.getImage().getFilePath());
        }
    }

    private Map<String, List<AttachImage>> separateImageList(Board board) {
        List<AttachImage> dbAttachImage = attachImageRepository.findAllByBoard(board);
        List<String> contentFilePath = getContentFiles(board.getWriting().getContent());

        List<AttachImage> confirmImage = new ArrayList<>();
        List<AttachImage> removeImage = new ArrayList<>();

        for (AttachImage img : dbAttachImage) {
            boolean flag = false;
            for (String path : contentFilePath) {
                if(img.getImage().getFilePath().contains(path))
                    flag = true;
            }

            if(flag) confirmImage.add(img);
            else removeImage.add(img);
        }

        Map<String, List<AttachImage>> result = new HashMap<>();
        result.put("confirm", confirmImage);
        result.put("remove", removeImage);
        return result;
    }
    private List<String> getContentFiles(String content) {
        List<String> result = new ArrayList<>();
        String regex = "\\!\\[[\\S]*\\]\\([\\S]*\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()) {
            String find = matcher.group();
            log.info("find images url = {}", find);
            result.add(find.substring(find.indexOf("/images") + 7, find.length() - 1));
        }
        return result;
    }


}
