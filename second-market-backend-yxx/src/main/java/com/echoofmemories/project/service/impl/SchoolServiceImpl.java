package com.echoofmemories.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.dto.SchoolRequest;
import com.echoofmemories.project.entity.School;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.SchoolMapper;
import com.echoofmemories.project.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

    private final SchoolMapper schoolMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public School addSchool(SchoolRequest request) {
        if (StrUtil.isBlank(request.getNameZh()) || StrUtil.isBlank(request.getNameKo())) {
            throw new CustomException("学校名称不能为空");
        }

        School school = new School();
        school.setSchoolCode(request.getSchoolCode());
        school.setNameZh(request.getNameZh());
        school.setNameKo(request.getNameKo());
        school.setNameEn(request.getNameEn());
        school.setCityZh(request.getCityZh());
        school.setCityKo(request.getCityKo());
        school.setCityEn(request.getCityEn());
        school.setLogoUrl(request.getLogoUrl());
        school.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        school.setDeleted(0);

        this.save(school);
        return school;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchool(SchoolRequest request) {
        if (request.getId() == null) {
            throw new CustomException("学校ID不能为空");
        }

        School school = this.getById(request.getId());
        if (school == null || school.getDeleted() == 1) {
            throw new CustomException("学校不存在");
        }

        school.setSchoolCode(request.getSchoolCode());
        school.setNameZh(request.getNameZh());
        school.setNameKo(request.getNameKo());
        school.setNameEn(request.getNameEn());
        school.setCityZh(request.getCityZh());
        school.setCityKo(request.getCityKo());
        school.setCityEn(request.getCityEn());
        school.setLogoUrl(request.getLogoUrl());

        if (request.getStatus() != null) {
            school.setStatus(request.getStatus());
        }

        return this.updateById(school);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchool(Long id) {
        School school = this.getById(id);
        if (school == null || school.getDeleted() == 1) {
            throw new CustomException("学校不存在");
        }
        return this.removeById(id);
    }

    @Override
    public School getSchoolById(Long id) {
        return this.getById(id);
    }

    @Override
    public Page<School> getSchoolPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<School> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getDeleted, 0);

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(School::getNameZh, keyword)
                    .or()
                    .like(School::getNameKo, keyword)
                    .or()
                    .like(School::getNameEn, keyword));
        }

        wrapper.orderByDesc(School::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<School> getSchoolList(String language) {
        ensureKoreanSchools();
        return schoolMapper.selectSchoolOptions(language);
    }

    private void ensureKoreanSchools() {
        List<School> seeds = new ArrayList<>();

        School kangwon = new School();
        kangwon.setSchoolCode("KNU_GANGWON");
        kangwon.setNameZh("江原大学");
        kangwon.setNameKo("강원대학교");
        kangwon.setNameEn("Kangwon National University");
        kangwon.setCityZh("春川");
        kangwon.setCityKo("춘천");
        kangwon.setCityEn("Chuncheon");
        kangwon.setStatus(1);
        kangwon.setDeleted(0);
        seeds.add(kangwon);

        School inha = new School();
        inha.setSchoolCode("INHA");
        inha.setNameZh("仁荷大学");
        inha.setNameKo("인하대학교");
        inha.setNameEn("Inha University");
        inha.setCityZh("仁川");
        inha.setCityKo("인천");
        inha.setCityEn("Incheon");
        inha.setStatus(1);
        inha.setDeleted(0);
        seeds.add(inha);

        School konkuk = new School();
        konkuk.setSchoolCode("KONKUK");
        konkuk.setNameZh("建国大学");
        konkuk.setNameKo("건국대학교");
        konkuk.setNameEn("Konkuk University");
        konkuk.setCityZh("首尔");
        konkuk.setCityKo("서울");
        konkuk.setCityEn("Seoul");
        konkuk.setStatus(1);
        konkuk.setDeleted(0);
        seeds.add(konkuk);

        School dankook = new School();
        dankook.setSchoolCode("DANKOOK");
        dankook.setNameZh("檀国大学");
        dankook.setNameKo("단국대학교");
        dankook.setNameEn("Dankook University");
        dankook.setCityZh("龙仁");
        dankook.setCityKo("용인");
        dankook.setCityEn("Yongin");
        dankook.setStatus(1);
        dankook.setDeleted(0);
        seeds.add(dankook);

        School uos = new School();
        uos.setSchoolCode("UOS");
        uos.setNameZh("首尔市立大学");
        uos.setNameKo("서울시립대학교");
        uos.setNameEn("University of Seoul");
        uos.setCityZh("首尔");
        uos.setCityKo("서울");
        uos.setCityEn("Seoul");
        uos.setStatus(1);
        uos.setDeleted(0);
        seeds.add(uos);

        School hufs = new School();
        hufs.setSchoolCode("HUFS");
        hufs.setNameZh("韩国外国语大学");
        hufs.setNameKo("한국외국어대학교");
        hufs.setNameEn("Hankuk University of Foreign Studies");
        hufs.setCityZh("首尔");
        hufs.setCityKo("서울");
        hufs.setCityEn("Seoul");
        hufs.setStatus(1);
        hufs.setDeleted(0);
        seeds.add(hufs);

        School cnu = new School();
        cnu.setSchoolCode("CNU");
        cnu.setNameZh("全南大学");
        cnu.setNameKo("전남대학교");
        cnu.setNameEn("Chonnam National University");
        cnu.setCityZh("光州");
        cnu.setCityKo("광주");
        cnu.setCityEn("Gwangju");
        cnu.setStatus(1);
        cnu.setDeleted(0);
        seeds.add(cnu);

        School jbnu = new School();
        jbnu.setSchoolCode("JBNU");
        jbnu.setNameZh("全北大学");
        jbnu.setNameKo("전북대학교");
        jbnu.setNameEn("Chonbuk National University");
        jbnu.setCityZh("全州");
        jbnu.setCityKo("전주");
        jbnu.setCityEn("Jeonju");
        jbnu.setStatus(1);
        jbnu.setDeleted(0);
        seeds.add(jbnu);

        School cnnu = new School();
        cnnu.setSchoolCode("CNNU");
        cnnu.setNameZh("忠南大学");
        cnnu.setNameKo("충남대학교");
        cnnu.setNameEn("Chungnam National University");
        cnnu.setCityZh("大田");
        cnnu.setCityKo("대전");
        cnnu.setCityEn("Daejeon");
        cnnu.setStatus(1);
        cnnu.setDeleted(0);
        seeds.add(cnnu);

        School gnu = new School();
        gnu.setSchoolCode("GNU");
        gnu.setNameZh("庆尚国立大学");
        gnu.setNameKo("경상국립대학교");
        gnu.setNameEn("Gyeongsang National University");
        gnu.setCityZh("晋州");
        gnu.setCityKo("진주");
        gnu.setCityEn("Jinju");
        gnu.setStatus(1);
        gnu.setDeleted(0);
        seeds.add(gnu);

        School jnu = new School();
        jnu.setSchoolCode("JNU");
        jnu.setNameZh("济州大学");
        jnu.setNameKo("제주대학교");
        jnu.setNameEn("Jeju National University");
        jnu.setCityZh("济州");
        jnu.setCityKo("제주");
        jnu.setCityEn("Jeju");
        jnu.setStatus(1);
        jnu.setDeleted(0);
        seeds.add(jnu);

        School cbnu = new School();
        cbnu.setSchoolCode("CBNU");
        cbnu.setNameZh("忠北大学");
        cbnu.setNameKo("충북대학교");
        cbnu.setNameEn("Chungbuk National University");
        cbnu.setCityZh("清州");
        cbnu.setCityKo("청주");
        cbnu.setCityEn("Cheongju");
        cbnu.setStatus(1);
        cbnu.setDeleted(0);
        seeds.add(cbnu);

        Set<String> codes = new HashSet<>();
        for (School s : seeds) {
            codes.add(s.getSchoolCode());
        }

        LambdaQueryWrapper<School> existsWrapper = new LambdaQueryWrapper<>();
        existsWrapper.eq(School::getDeleted, 0).in(School::getSchoolCode, codes);
        List<School> existing = this.list(existsWrapper);
        Set<String> existingCodes = new HashSet<>();
        for (School s : existing) {
            existingCodes.add(s.getSchoolCode());
        }

        List<School> toInsert = new ArrayList<>();
        for (School s : seeds) {
            if (!existingCodes.contains(s.getSchoolCode())) {
                toInsert.add(s);
            }
        }

        if (!toInsert.isEmpty()) {
            this.saveBatch(toInsert);
        }
    }
}
