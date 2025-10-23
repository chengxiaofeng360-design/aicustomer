package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.SocialPost;
import com.aicustomer.service.SocialPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交动态控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/social")
public class SocialPostController {

    @Autowired
    private SocialPostService socialPostService;

    /**
     * 获取动态列表
     */
    @GetMapping("/posts")
    public Result<List<SocialPost>> getPosts(@RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<SocialPost> posts = socialPostService.getPosts(page, size);
            return Result.success(posts);
        } catch (Exception e) {
            return Result.error("获取动态列表失败: " + e.getMessage());
        }
    }

    /**
     * 发布动态
     */
    @PostMapping("/posts")
    public Result<SocialPost> createPost(@RequestBody SocialPost socialPost) {
        try {
            SocialPost result = socialPostService.createPost(socialPost);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("发布动态失败: " + e.getMessage());
        }
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/posts/{postId}/like")
    public Result<String> toggleLike(@PathVariable Long postId,
                                    @RequestParam Long customerAccountId) {
        try {
            boolean liked = socialPostService.toggleLike(postId, customerAccountId);
            return Result.success(liked ? "点赞成功" : "取消点赞成功");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取动态详情
     */
    @GetMapping("/posts/{postId}")
    public Result<SocialPost> getPost(@PathVariable Long postId) {
        try {
            SocialPost post = socialPostService.getPostById(postId);
            if (post != null) {
                return Result.success(post);
            } else {
                return Result.error("动态不存在");
            }
        } catch (Exception e) {
            return Result.error("获取动态失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的动态列表
     */
    @GetMapping("/posts/user/{userId}")
    public Result<List<SocialPost>> getUserPosts(@PathVariable Long userId) {
        try {
            List<SocialPost> posts = socialPostService.getPostsByUserId(userId);
            return Result.success(posts);
        } catch (Exception e) {
            return Result.error("获取用户动态失败: " + e.getMessage());
        }
    }
}
