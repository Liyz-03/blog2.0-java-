package com.hiworlds.bbblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiworlds.bbblog.domain.Post;
import com.hiworlds.bbblog.domain.admin.PostTotal;
import com.hiworlds.bbblog.mapper.PostDao;
import com.hiworlds.bbblog.myException.errorMsgConstant.ResponseMsgConstant;
import com.hiworlds.bbblog.myException.myExceptions.RequestParamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Liyz
 * @Date: 2020/7/22 16:18
 * @Description:文章控制器
 **/

@RestController
public class PostController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostDao postDao;
    private Logger logger = LoggerFactory.getLogger(PostController.class);

    /**
     * 博客首页查询所有文章
     *
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/home/findAllPosts")
    public String findAllPosts() throws JsonProcessingException {
        System.out.println("findAllPosts...");
        List<Post> allPosts = postDao.findAllPosts();
        System.out.println(allPosts);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("msg", "文章查询成功");
        resultMap.put("posts", allPosts);
        return objectMapper.writeValueAsString(resultMap);
    }

    /**
     * 通过id查询文章细节
     *
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/home/findPostById")
    public String findPostById(@RequestParam String id) throws JsonProcessingException {
        System.out.println(id);
        if (id == null) {
            throw new RequestParamsException();
        }
        Post postById = postDao.findPostById(Integer.parseInt(id));
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("msg", "文章查询成功");
        resultMap.put("post", postById);
        return objectMapper.writeValueAsString(resultMap);
    }

    /**
     * 获取分类文章
     *
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/home/findAllPostsByCategoryId")
    public String findAllPostsByCategoryId(String id) throws JsonProcessingException {
        logger.debug("/home/findAllPostsByCategoryId获取id---" + id);
        Map<String, Object> map = new HashMap<>();
        if (id == null) {
            throw new RequestParamsException(ResponseMsgConstant.ERROR_MSG_PARAMS_EXCPTION);
        }
        List<Post> postsByCategoryId = postDao.findAllPostsByCategoryId(Integer.parseInt(id));
        map.put("code", 200);
        map.put("msg", "获取分类文章成功");
        map.put("posts", postsByCategoryId);
        return objectMapper.writeValueAsString(map);
    }

    /**
     * 保存文章
     *
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/admin/savePost")
    public String savePost(@RequestBody Map params) throws Exception {
        logger.debug("savePost处理请求。。。");
        Post savePost = new Post();
        // {title=达娃大, description=达瓦达瓦, content=达瓦达瓦,
        // isDraft=true, create_time=1594802970000, author_id=1, author_name=Liyz, category_id=20, category_name=其他}
        try {
            savePost.setPost_title((String) params.get("title"));
            savePost.setPost_description((String) params.get("description"));
            savePost.setPost_content((String) params.get("content"));
            savePost.setPost_is_draft((Boolean) params.get("isDraft"));
            savePost.setPost_create_time(new Date((long) params.get("create_time")));
            savePost.setPost_author_id((Integer) params.get("author_id"));
            savePost.setUser_name((String) params.get("author_name"));
            savePost.setPost_category_id((Integer) params.get("category_id"));
            savePost.setCategory_name((String) params.get("category_name"));
            if ((Boolean) params.get("isDraft") == false) {
                savePost.setPost_public_time(new Date((long) params.get("create_time")));
            }
        } catch (Exception e) {
            throw new Exception(ResponseMsgConstant.ERROR_MSG_PARAMS_EXCPTION);
        }
        logger.debug("即将保存的文章Bean： " + savePost.toString());
        postDao.savePost(savePost);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "发布成功！");
        map.put("data", null);
        return objectMapper.writeValueAsString(map);
    }

    /**
     * 查询所有文章包括草稿
     *
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/admin/findAllPostsIncludeDraft")
    public String findAllPostsIncludeDraft() throws JsonProcessingException {
        List<Post> allPosts = postDao.findAllPostsIncludeDraft();
        System.out.println(allPosts);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("msg", "文章查询成功");
        resultMap.put("posts", allPosts);
        return objectMapper.writeValueAsString(resultMap);
    }

    /**
     * 修改文章状态
     *
     * @param params
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/admin/changePostIsDraft")
    public String changePostIsDraft(@RequestBody Map<String, Object> params) throws JsonProcessingException {
        Boolean isDraft = (Boolean) params.get("post_is_draft");
        postDao.changePostIsDraft((Integer) params.get("post_id"), (Boolean) params.get("post_is_draft"), new Date());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("msg", "文章修改成功");
        resultMap.put("data", null);
        return objectMapper.writeValueAsString(resultMap);
    }

    /**
     * 修改文章内容
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/admin/changePostContent")
    public String changePostContent(@RequestBody Map params) throws Exception {
        logger.debug("changePostContent接收的参数:" + params);
        Post post = new Post();
        try {
            post.setPost_id((Integer) params.get("post_author_id"));
            post.setPost_title((String) params.get("post_title"));
            post.setPost_description((String) params.get("post_description"));
            post.setPost_content((String) params.get("post_content"));
            post.setPost_public_time(new Date());
        } catch (Exception e) {
            throw new Exception(ResponseMsgConstant.ERROR_MSG_PARAMS_EXCPTION);
        }
        postDao.changePostContent(post);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("msg", "文章修改成功");
        resultMap.put("data", null);
        return objectMapper.writeValueAsString(resultMap);
    }

    /**
     * 删除文章
     * @param post_id
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/admin/deletePost")
    public String deletePostById(@RequestParam("id") Integer post_id) throws JsonProcessingException {
        postDao.deletePostById(post_id);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("msg", "文章删除成功");
        resultMap.put("data", null);
        return objectMapper.writeValueAsString(resultMap);
    }

    @GetMapping("/admin/getTotal")
    public String getTotal() throws JsonProcessingException {
        int postTotal =  postDao.getTotal();
        int allDraft = postDao.getAllDraft();
        PostTotal sendPostTotal = new PostTotal();
        sendPostTotal.setAllPost(postTotal);
        sendPostTotal.setDraft(allDraft);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("msg", "获取成功");
        resultMap.put("data", sendPostTotal);
        return objectMapper.writeValueAsString(resultMap);
    }

}
