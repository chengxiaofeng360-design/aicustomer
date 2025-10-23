package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.SocialComment;
import com.aicustomer.service.SocialCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交评论控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/social")
public class SocialCommentController {

    @Autowired
    private SocialCommentService socialCommentService;

    /**
     * 获取动态的评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public Result<List<SocialComment>> getComments(@PathVariable Long postId) {
        try {
            List<SocialComment> comments = socialCommentService.getCommentsByPostId(postId);
            return Result.success(comments);
        } catch (Exception e) {
            return Result.error("获取评论失败: " + e.getMessage());
        }
    }

    /**
     * 添加评论
     */
    @PostMapping("/posts/{postId}/comments")
    public Result<SocialComment> addComment(@PathVariable Long postId,
                                          @RequestBody SocialComment comment) {
        try {
            comment.setPostId(postId);
            SocialComment result = socialCommentService.addComment(comment);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("添加评论失败: " + e.getMessage());
        }
    }

    /**
     * 点赞/取消点赞评论
     */
    @PostMapping("/comments/{commentId}/like")
    public Result<String> toggleCommentLike(@PathVariable Long commentId,
                                          @RequestParam Long customerAccountId) {
        try {
            boolean liked = socialCommentService.toggleLike(commentId, customerAccountId);
            return Result.success(liked ? "点赞成功" : "取消点赞成功");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的评论列表
     */
    @GetMapping("/comments/user/{userId}")
    public Result<List<SocialComment>> getUserComments(@PathVariable Long userId) {
        try {
            List<SocialComment> comments = socialCommentService.getCommentsByUserId(userId);
            return Result.success(comments);
        } catch (Exception e) {
            return Result.error("获取用户评论失败: " + e.getMessage());
        }
    }
}
