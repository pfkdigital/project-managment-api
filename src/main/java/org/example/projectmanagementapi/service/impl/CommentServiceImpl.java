package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.CommentDto;
import org.example.projectmanagementapi.entity.*;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.repository.CommentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.CommentService;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public Comment createComment(CommentDto comment) {
        User author = findUserById(comment.getAuthorId());
        Issue issue = findIssueById(comment.getIssueId());
        Task task = findTaskById(comment.getTaskId());

        Comment newComment = Comment.builder()
                .content(comment.getContent())
                .createdAt(LocalDate.now())
                .author(author)
                .issue(issue)
                .task(task)
                .build();

        author.addComment(newComment);

        if (issue != null) {
            issue.addComment(newComment);
        }

        if (task != null) {
            task.addComment(newComment);
        }

        Comment savedComment = commentRepository.save(newComment);

        notificationService.createNotification("Comment " + savedComment.getId() + " has been created", NotificationType.CREATION);

        return savedComment;
    }

    @Override
    public Comment getComment(Integer commentId) {
        return findCommentById(commentId);
    }

    @Override
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment updateComment(Integer commentId, CommentDto comment) {
        Comment existingComment = findCommentById(commentId);
        existingComment.setContent(comment.getContent());
        existingComment.setUpdatedAt(LocalDate.now());

        notificationService.createNotification("Comment " + existingComment.getId() + " has been updated", NotificationType.UPDATE);

        return commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        Comment comment = findCommentById(commentId);
        commentRepository.delete(comment);

        notificationService.createNotification("Comment " + commentId + " has been deleted", NotificationType.DESTRUCTION);
    }

    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
    }

    private Comment findCommentById(Integer commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with id " + commentId + " not found"));
    }

    private Issue findIssueById(Integer issueId) {
        return issueRepository.findById(issueId)
                .orElse(null);
    }

    private Task findTaskById(Integer taskId) {
        return taskRepository.findById(taskId)
                .orElse(null);
    }
}
