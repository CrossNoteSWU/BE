//package com.swulion.crossnote.dto.Curation;
//
//// XML 파싱에 필요한 어노테이션들을 import 합니다.
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import java.util.List;
//
///**
// * KCI API 응답 DTO (XML 파싱용으로 수정됨)
// * XML 구조: <outputData> <record> <articleInfo> ... </articleInfo> </record> </outputData>
// */
//@Getter
//@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JacksonXmlRootElement(localName = "outputData") // 1. 이 DTO가 <outputData> 루트 태그에 매핑
//public class KciResponseDto {
//
//    // 2. <record> 리스트 매핑
//    // <outputData> 바로 밑에 <record>가 반복되므로 래퍼(wrapper)가 없는 리스트입니다.
//    @JacksonXmlElementWrapper(useWrapping = false)
//    @JacksonXmlProperty(localName = "record")
//    private List<Record> records;
//
//    // (기존의 중첩된 OutputData 클래스는 제거하고, records 필드를 루트 DTO로 올렸습니다)
//
//    @Getter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class Record {
//        @JacksonXmlProperty(localName = "articleInfo")
//        private ArticleInfo articleInfo;
//    }
//
//    @Getter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class ArticleInfo {
//        @JacksonXmlProperty(localName = "title-group")
//        private TitleGroup titleGroup;
//
//        @JacksonXmlProperty(localName = "abstract-group")
//        private AbstractGroup abstractGroup;
//
//        @JacksonXmlProperty(localName = "url")
//        private String url;
//    }
//
//    @Getter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class TitleGroup {
//        // <article-title>... </article-title>
//        // <article-title>... </article-title>
//        @JacksonXmlElementWrapper(useWrapping = false)
//        @JacksonXmlProperty(localName = "article-title")
//        private List<LangText> articleTitles;
//    }
//
//    @Getter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class AbstractGroup {
//        // <abstract>...</abstract>
//        // <abstract>...</abstract>
//        @JacksonXmlElementWrapper(useWrapping = false)
//        @JacksonXmlProperty(localName = "abstract")
//        private List<LangText> abstracts;
//    }
//
//    /**
//     * <article-title lang="original">여기가 텍스트 값(value)</article-title>
//     * 위와 같은 XML 태그를 매핑합니다.
//     */
//    @Getter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class LangText {
//
//        // 3. 'lang' 속성 매핑 (isAttribute = true)
//        @JacksonXmlProperty(localName = "lang", isAttribute = true)
//        private String lang;
//
//        // 4. 태그 내부의 텍스트 값 매핑 (@JacksonXmlText)
//        @JacksonXmlText
//        private String value;
//    }
//}