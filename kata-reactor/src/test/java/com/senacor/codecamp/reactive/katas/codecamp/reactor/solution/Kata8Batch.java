package com.senacor.codecamp.reactive.katas.codecamp.reactor.solution;

import com.senacor.codecamp.reactive.services.PersistService;
import com.senacor.codecamp.reactive.services.WikiService;
import com.senacor.codecamp.reactive.util.ReactiveUtil;
import com.senacor.codecamp.reactive.util.WaitMonitor;
import org.junit.Test;
import reactor.core.Disposable;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * @author Andreas Keefer
 */
public class Kata8Batch {

    private final WikiService wikiService = WikiService.create();
    private final PersistService persistService = PersistService.create();

    /**
     * 1. use {@link WikiService#wikiArticleBeingReadFluxBurst()} that returns a stream of wiki article being read
     * 2. watch the stream 2 sec
     * 3. save the article ({@link PersistService#save(String)}). The service returns the execution time
     * 4. sum the execution time of the service calls and print the result
     */
    @Test
    public void withoutBatch() throws Exception {
        final WaitMonitor monitor = new WaitMonitor();

        Disposable subscribe = wikiService.wikiArticleBeingReadFluxBurst()
                .take(Duration.of(2, SECONDS))
                .doOnNext(ReactiveUtil::print)
                .map(persistService::save)
                .reduce((l, r) -> l + r)
                .subscribe(next -> ReactiveUtil.print("save runtime (SUM): %s ms", next),
                        Throwable::printStackTrace,
                        () -> {
                            ReactiveUtil.print("complete!");
                            monitor.complete();
                        });

        monitor.waitFor(10, TimeUnit.SECONDS);
        subscribe.dispose();
    }

    @Test
    public void batch() throws Exception {
        // 1. do the same as above, but this time use the method #save(Iterable) to save a batch of articles.
        //    use a batch size of 5.
        //    Please note that this is a stream - you can not wait until all articles are delivered to save everything in a batch
        // 2. If the batch size is not reached within 500 milliseconds,
        //    flush the buffer anyway by writing to the service
        final WaitMonitor monitor = new WaitMonitor();

        Disposable subscribe = wikiService.wikiArticleBeingReadFluxBurst()
                .take(Duration.of(2, SECONDS))
                .doOnNext(ReactiveUtil::print)
                .bufferTimeout(5, Duration.of(500, MILLIS))
                .map(persistService::save)
                .reduce((l, r) -> l + r)
                .subscribe(next -> ReactiveUtil.print("save runtime (SUM): %s ms", next),
                        Throwable::printStackTrace,
                        () -> {
                            ReactiveUtil.print("complete!");
                            monitor.complete();
                        });

        monitor.waitFor(10, TimeUnit.SECONDS);
        subscribe.dispose();
    }
}