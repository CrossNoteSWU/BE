//package com.swulion.crossnote.dto.Curation;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class Item { // ✅ public 클래스 (파일 이름과 일치)
//
//    @JacksonXmlProperty(localName = "title_info")
//    private String titleInfo;
//
//    @JacksonXmlProperty(localName = "author_info")
//    private String authorInfo;
//
//    @JacksonXmlProperty(localName = "detail_link")
//    private String detailLink;
//
//    @JacksonXmlProperty(localName = "type_name")
//    private String typeName;
//
//    @JacksonXmlProperty(localName = "place_info")
//    private String placeInfo;
//
//    @JacksonXmlProperty(localName = "pub_info")
//    private String pubInfo;
//}