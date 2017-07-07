package com.senacor.codecamp.reactive.katas.codecamp.rxjava2;

import com.senacor.codecamp.reactive.services.WikiService;
import com.senacor.codecamp.reactive.katas.KataClassification;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import io.reactivex.Observable;
import org.junit.Test;

import static com.senacor.codecamp.reactive.katas.KataClassification.Classification.*;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Andreas Keefer
 */
public class Kata2BasicOperators {

    private WikiService wikiService = WikiService.create();

    @Test
    @KataClassification(mandatory)
    public void basicsA() throws Exception {
        // 1. Use the WikiService (fetchArticleObservable) and fetch an arbitrary wikipedia article
        // 2. transform the result with the WikiService#parseMediaWikiText to an object structure
        //    and print out the first paragraph

        final String topic = "Vaporwave";
        wikiService.fetchArticleObservable(topic)
                .map(wikiService::parseMediaWikiText)
                .map(page -> page.getFirstParagraph())
                .map(p -> p.getText())
                .test()
                .awaitDone(1, SECONDS)
                .assertValueCount(1)
                .assertValue(text -> text.contains(topic))
                .assertComplete();
    }

    @Test
    @KataClassification(advanced)
    public void basicsB() throws Exception {
        // 3. split the Article (ParsedPage.getText()) in words (separator=" ")
        // 4. sum the number of letters of all words beginning with character 'a' to the console

        final String topic = "Vaporwave";
        wikiService.fetchArticleObservable(topic)
                .map(wikiService::parseMediaWikiText)
                .map(ParsedPage::getText)
                .flatMap(t -> Observable.fromArray(t.split(" ")))
                .filter(w -> w.startsWith("a"))
                .doOnNext(System.out::println)
                .map(String::length)
                .doOnNext(System.out::println)
                .reduce(0, (acc, len) -> acc + len)
                .doOnSuccess(System.out::println)
                .test()
                .awaitDone(1, SECONDS)
                .assertValueCount(1)
                .assertComplete();
    }

    @Test
    @KataClassification(hardcore)
    public void basicsC() throws Exception {
        // 5. filter out redundant words beginning with 'a'
        // 6. order them by length and take only the top 10 words in length

        //  wikiService.fetchArticleObservable()
    }
}
