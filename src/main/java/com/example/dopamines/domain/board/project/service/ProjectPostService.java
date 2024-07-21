package com.example.dopamines.domain.board.project.service;

import com.example.dopamines.domain.board.project.model.entity.ProjectPost;
import com.example.dopamines.domain.board.project.model.request.ProjectPostReq;
import com.example.dopamines.domain.board.project.model.request.ProjectPostUpdateReq;
import com.example.dopamines.domain.board.project.model.response.ProjectPostRes;
import com.example.dopamines.domain.board.project.model.response.ProjectPostReadRes;
import com.example.dopamines.domain.board.project.repository.ProjectBoardRepository;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.domain.user.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectPostService {

    private final ProjectBoardRepository projectBoardRepository;
    private final TeamRepository teamRepository;

    public ProjectPostRes create(String title, String content, Integer courseNum, Long teamIdx, String savedFileName) {

        ProjectPost projectBoard = ProjectPost.builder()
                .title(title)
                .contents(content)
                .courseNum(courseNum)
                .sourceUrl(savedFileName)
                .team(teamRepository.findById(teamIdx).get())
                .build();

        projectBoard = projectBoardRepository.save(projectBoard);

        List<String> students = new ArrayList<>();
        for(User student : projectBoard.getTeam().getStudents()) {
            students.add(student.getName());
        }

        return ProjectPostRes.builder()
                .idx(projectBoard.getIdx())
                .title(projectBoard.getTitle())
                .contents(projectBoard.getContents())
                .courseNum(projectBoard.getCourseNum())
                .sourceUrl(projectBoard.getSourceUrl())
                .teamName(projectBoard.getTeam().getTeamName())
                .students(students)
                .build();
    }

    public ProjectPostReadRes read(Long idx) {

        Optional<ProjectPost> result = projectBoardRepository.findById(idx);

        if(result.isPresent()) {
            ProjectPost projectBoard = result.get();

            List<String> students = new ArrayList<>();
            for(User student : projectBoard.getTeam().getStudents()) {
                students.add(student.getName());
            }

            return ProjectPostReadRes.builder()
                    .idx(projectBoard.getIdx())
                    .title(projectBoard.getTitle())
                    .contents(projectBoard.getContents())
                    .courseNum(projectBoard.getCourseNum())
                    .sourceUrl(projectBoard.getSourceUrl())
                    .teamName(projectBoard.getTeam().getTeamName())
                    .students(students)
                    .build();
        } else {
            return null;
        }
    }

    public List<ProjectPostReadRes> readByCourseNum(Long courseNum) {

        List<ProjectPost> projectList = projectBoardRepository.findByCourseNum(courseNum);

        List<ProjectPostReadRes> res = new ArrayList<>();

        for(ProjectPost projectPost : projectList) {
            List<String> students = new ArrayList<>();
            for(User student : projectPost.getTeam().getStudents()) {
                students.add(student.getName());
            }

            res.add(ProjectPostReadRes.builder()
                    .idx(projectPost.getIdx())
                    .title(projectPost.getTitle())
                    .contents(projectPost.getContents())
                    .courseNum(projectPost.getCourseNum())
                    .teamName(projectPost.getTeam().getTeamName())
                    .students(students)
                    .build());
        }

        return res;
    }

    public List<ProjectPostReadRes> readAll() {

        List<ProjectPost> projectList = projectBoardRepository.findAll();

        List<ProjectPostReadRes> res = new ArrayList<>();

        for(ProjectPost projectPost : projectList) {
            List<String> students = new ArrayList<>();
            for(User student : projectPost.getTeam().getStudents()) {
                students.add(student.getName());
            }

            res.add(ProjectPostReadRes.builder()
                    .idx(projectPost.getIdx())
                    .title(projectPost.getTitle())
                    .contents(projectPost.getContents())
                    .courseNum(projectPost.getCourseNum())
                    .teamName(projectPost.getTeam().getTeamName())
                    .students(students)
                    .build());
        }

        return res;
    }

    public ProjectPostReadRes update(ProjectPostUpdateReq req, String savedFileName) {
        ProjectPost projectPost = ProjectPost.builder()
                .idx(req.getIdx())
                .title(req.getTitle())
                .contents(req.getContents())
                .courseNum(req.getCourseNum())
                .sourceUrl(savedFileName)
                .team(teamRepository.findById(req.getTeamIdx()).get())
                .build();

        projectPost = projectBoardRepository.save(projectPost);

        List<String> students = new ArrayList<>();
        for(User student : projectPost.getTeam().getStudents()) {
            students.add(student.getName());
        }

        return ProjectPostReadRes.builder()
                .idx(projectPost.getIdx())
                .title(projectPost.getTitle())
                .contents(projectPost.getContents())
                .courseNum(projectPost.getCourseNum())
                .sourceUrl(projectPost.getSourceUrl())
                .teamName(projectPost.getTeam().getTeamName())
                .students(students)
                .build();
    }

    public void delete(Long idx) {
        projectBoardRepository.deleteById(idx);
    }

}
