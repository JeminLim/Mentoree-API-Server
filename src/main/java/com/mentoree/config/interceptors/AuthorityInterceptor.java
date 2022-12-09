package com.mentoree.config.interceptors;


import com.mentoree.config.security.AuthenticateUser;
import com.mentoree.domain.entity.Board;
import com.mentoree.domain.entity.Mentee;
import com.mentoree.domain.entity.Mentor;
import com.mentoree.domain.entity.Mission;
import com.mentoree.domain.repository.BoardRepository;
import com.mentoree.domain.repository.MenteeRepository;
import com.mentoree.domain.repository.MentorRepository;
import com.mentoree.domain.repository.MissionRepository;
import com.mentoree.exception.NoDataFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthorityInterceptor implements HandlerInterceptor {

    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final MissionRepository missionRepository;
    private final BoardRepository boardRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Authority auth = handlerMethod.getMethodAnnotation(Authority.class);

        if(auth == null)
            return true;

        AuthenticateUser loginMember
                = (AuthenticateUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Map<?, ?> pathVariable = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long programId =
                pathVariable.containsKey("programId") ? Long.parseLong((String) pathVariable.get("programId")) : null;
        Long missionId =
                pathVariable.containsKey("missionId") ? Long.parseLong((String) pathVariable.get("missionId")) : null;
        Long boardId =
                pathVariable.containsKey("boardId") ? Long.parseLong((String) pathVariable.get("boardId")) : null;

        switch (auth.role()) {
            case HOST:
                return isHost(programId, loginMember.getId());

            case MENTOR:
                return isMentor(request, programId, loginMember.getId());

            case WRITER:
                return isWriter(missionId, boardId, loginMember.getId());

            case PARTICIPANT:
                return isParticipant(request, programId, missionId, boardId, loginMember.getId());
            default:
                return true;
        }
    }

    private boolean isHost(Long programId, Long memberId) {
        Mentor findMentor = mentorRepository.findByProgramIdAndMemberId(programId, memberId)
                .orElseThrow(NoDataFoundException::new);
        return findMentor.getHost();
    }

    private boolean isMentor(HttpServletRequest request, @Nullable Long programId, Long memberId) {
        if(programId == null) {
            programId = Long.parseLong(request.getParameter("programId"));
        }
        Optional<Mentor> findMentor = mentorRepository.findByProgramIdAndMemberId(programId, memberId);
        return !findMentor.isEmpty();
    }

    private boolean isWriter(@Nullable Long missionId, @Nullable Long boardId, Long memberId) {
        if(missionId != null) {
            Mission findMission = missionRepository.findById(missionId).orElseThrow(NoDataFoundException::new);
            return memberId == findMission.getWriter().getId();
        }

        if(boardId != null) {
            Board findBoard = boardRepository.findById(boardId).orElseThrow(NoDataFoundException::new);
            return memberId == findBoard.getWriter().getId();
        }

        return false;
    }

    private boolean isParticipant(HttpServletRequest request, @Nullable Long programId, @Nullable Long missionId, @Nullable Long boardId, Long memberId) {
        if(programId != null) {
            return findMentorOrMentee(programId, memberId);
        }

        if(missionId != null) {
            Mission findMission = missionRepository.findById(missionId).orElseThrow(NoDataFoundException::new);
            return findMentorOrMentee(findMission.getProgram().getId(), memberId);
        }

        if(boardId != null) {
            Board board = boardRepository.fetchFindById(boardId).orElseThrow(NoDataFoundException::new);
            return findMentorOrMentee(board.getMission().getProgram().getId(), memberId);
        }

        long formMissionId = Long.parseLong(request.getParameter("missionId"));
        Mission findMission = missionRepository.findById(formMissionId).orElseThrow(NoDataFoundException::new);
        return findMentorOrMentee(findMission.getProgram().getId(), memberId);
    }

    private boolean findMentorOrMentee(Long programId, Long memberId) {
        Optional<Mentor> findMentor = mentorRepository.findByProgramIdAndMemberId(programId, memberId);
        Optional<Mentee> findMentee = menteeRepository.findByProgramIdAndMemberId(programId, memberId);
        return !findMentor.isEmpty() || !findMentee.isEmpty();
    }

}
