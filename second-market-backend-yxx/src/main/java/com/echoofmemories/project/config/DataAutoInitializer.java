package com.echoofmemories.project.config;

import com.echoofmemories.project.entity.CampusService;
import com.echoofmemories.project.entity.ForumComment;
import com.echoofmemories.project.entity.ForumPost;
import com.echoofmemories.project.service.CampusServiceService;
import com.echoofmemories.project.service.ForumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 校园数据自动初始化
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataAutoInitializer implements CommandLineRunner {

    private final ForumService forumService;
    private final CampusServiceService campusServiceService;

    @Override
    public void run(String... args) {
        if (forumService.count() == 0) {
            initForumData();
        }
        if (campusServiceService.count() == 0) {
            initCampusServiceData();
        }
    }

    private void initForumData() {
        log.info("开始初始化论坛演示数据...");
        Long[] userIds = {2L, 3L, 4L, 6L};
        Long schoolId = 13L;

        String[] titles = {
            "大家觉得学校北门的麻辣烫哪家好吃？",
            "求助！在图书馆四楼捡到一个灰色钱包，内有学生卡",
            "吐槽一下，最近学校的Wi-Fi信号也太差了吧...",
            "有学长学姐带带考研吗？计科专业的",
            "分享一下我最近在校园发现的绝美落日拍摄点",
            "【转让】健身房会员卡一张，还有半年，骨折价"
        };
        String[] contents = {
            "北门新开的那家张亮感觉味道一般，还是老王麻辣烫更正宗一点，大家觉得呢？",
            "失主请私聊我确认钱包内的金额和姓名，我在图书馆服务台等。",
            "已经在宿舍断网两个小时了，IT中心的人在干嘛？有人知道怎么修吗？",
            "26届考研求带，主要想了解一下专业课的复习重点，重金求资料！",
            "就在教学楼C座顶楼，傍晚6点左右，光线绝了，附上几张原图。",
            "因为实习要去别的城市了，转让剩下半年的会员卡，有意者私聊。"
        };
        String[] categories = {"生活助手", "寻物启事", "吐槽建议", "学习交流", "校园生活", "二手交易"};
        boolean[] anonymities = {false, true, false, false, false, false};

        for (int i = 0; i < titles.length; i++) {
            ForumPost post = new ForumPost();
            post.setUserId(userIds[i % userIds.length]);
            post.setSchoolId(schoolId);
            post.setTitle(titles[i]);
            post.setContent(contents[i]);
            post.setCategory(categories[i]);
            post.setIsAnonymous(anonymities[i]);
            post.setCommentCount(0);
            post.setViewCount((int)(Math.random() * 100));
            forumService.createPost(post);

            if (i < 2) {
                ForumComment comment = new ForumComment();
                comment.setPostId(post.getId());
                comment.setUserId(userIds[(i + 1) % userIds.length]);
                comment.setContent("我觉得北门那家杨国福也不错！");
                comment.setIsAnonymous(false);
                forumService.addComment(comment);
            }
        }
        log.info("论坛数据初始化完成");
    }

    private void initCampusServiceData() {
        log.info("开始初始化校园跑腿演示数据...");
        Long[] userIds = {2L, 3L, 4L, 6L};
        Long schoolId = 13L;

        String[] titles = {
            "【跑腿】帮忙去北门菜鸟驿站拿个大件快递",
            "【拼单】食堂二楼炸酱面求拼，第二份半价",
            "【跑腿】求带一份校医室的感冒药，急！",
            "【拼车】周五下午3点出发去首尔火车站，3缺1",
            "【资源】求借一个大功率吹风机，用完即还"
        };
        String[] contents = {
            "大概5kg重，有点沉，送到宿舍楼下即可，报酬10元。",
            "想吃炸酱面了，有人一起拼吗？一个人吃不完两份。",
            "发烧了出不去，求好心人帮忙买个退烧贴和感冒药，重谢！",
            "已经叫好车了，车费平摊，大概每人8000韩币。",
            "宿舍的坏了，急需用一下，可以付少量租金。"
        };
        Integer[] types = {1, 2, 1, 2, 3};
        BigDecimal[] rewards = {new BigDecimal("10.00"), BigDecimal.ZERO, new BigDecimal("20.00"), BigDecimal.ZERO, new BigDecimal("5.00")};

        for (int i = 0; i < titles.length; i++) {
            CampusService service = new CampusService();
            service.setUserId(userIds[i % userIds.length]);
            service.setSchoolId(schoolId);
            service.setTitle(titles[i]);
            service.setContent(contents[i]);
            service.setType(types[i]);
            service.setReward(rewards[i]);
            service.setStatus(1);
            if (types[i] == 2) {
                service.setLimitCount(4);
                service.setCurrentCount(1);
            }
            service.setLocation("校内");
            campusServiceService.save(service);
        }
        log.info("校园跑腿数据初始化完成");
    }
}
