package com.senacor.tecco.reactive.concurrency.e5.streams;

import com.senacor.tecco.reactive.ReactiveUtil;
import com.senacor.tecco.reactive.concurrency.PlaneArticleBaseTest;
import com.senacor.tecco.reactive.concurrency.Summary;
import org.junit.Test;

import java.util.Arrays;

/**
 * Retrieves and combines plane information with streams
 *
 * @author Dr. Michael Menzel, Sencaor Technologies AG
 */
public class E52_Streams_CountPlanes extends PlaneArticleBaseTest {

    @Test
    public void thatPlaneBuildCountIsFetchedWithStreams() throws Exception {

        String[] planeTypes = {"Boeing 777", "Boeing 747"};

        Arrays.stream(planeTypes)
                .map(planeType -> fetchArticle(planeType))
                .map(article -> parsePlaneInfo(article))
                .forEach((planeInfo)-> {
                    Summary.printCounter(planeInfo.typeName, planeInfo.numberBuild);
                });
    }

    // fetches an article from Wikipedia
    Article fetchArticle(String articleName) {
        return new Article(articleName, wikiService.fetchArticle(articleName));
    }

    // Extracts plane-related information from an wikipedia article
    PlaneInfo parsePlaneInfo(Article article){
        return new PlaneInfo(article.name, ReactiveUtil.findValue(article.content, "number built"));
    }

    class Article{
        public String name;
        public String content;

        public Article(String name, String content) {
            this.name = name;
            this.content = content;
        }
    }

    class PlaneInfo{
        public String typeName;
        public String numberBuild;

        public PlaneInfo(String typeName, String numberBuild) {
            this.typeName = typeName;
            this.numberBuild = numberBuild;
        }

        @Override
        public String toString() {
            return "PlaneInfo{" +
                    "typeName='" + typeName + '\'' +
                    ", numberBuild='" + numberBuild + '\'' +
                    '}';
        }
    }

}