package com.app.XGOS;

import com.app.XGOS.converter.WishToOrderConverter;
import com.app.XGOS.sqlRepository.OrderPrivateData;
import com.app.XGOS.sqlRepository.WishDataRepository;
import com.app.XGOS.wishProvider.FromXmlWishIterativeProvider;
import com.app.XGOS.wishProvider.Wish;
import com.app.XGOS.wishProvider.WishProviderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class SchedulerServiceTest {

    @SpyBean
    SchedulerService schedulerService;
    @MockBean
    FromXmlWishIterativeProvider fromXmlWishIterativeProvider;
    @MockBean
    WishDataRepository wishDataRepository;
    @MockBean
    WishToOrderConverter wishToOrderConverter;

    @Test
    void givenRightInput_whenUpdateWishesAndOrdersDatabase_thenCheckInteractions(@TempDir Path tempDir) throws IOException, WishProviderException {
        Path path = tempDir.resolve("example.xml");
        Files.write(path, "whatever".getBytes());
        schedulerService.setWishSourceFilePathname(getAA(path.toString()));

        Mockito.when(fromXmlWishIterativeProvider.isMoreDataAvailable())
                .thenReturn(true, true, false);
        Mockito.when(wishToOrderConverter.getOrder(any()))
                .thenReturn(new OrderPrivateData(), new OrderPrivateData());
        Mockito.when(fromXmlWishIterativeProvider.getUpToNWishes(anyInt()))
                .thenReturn(singletonList(new Wish()), singletonList(new Wish()));

        schedulerService.updateWishesAndOrdersDatabase();
        verify(wishDataRepository, times(2)).saveAll(any());
    }

    @Test
    void givenWrongInput_whenUpdateWishesAndOrdersDatabase_thenNoInteractions() {
        schedulerService.setWishSourceFilePathname(getAA("whatever"));
        schedulerService.updateWishesAndOrdersDatabase();
        verifyNoInteractions(fromXmlWishIterativeProvider);
        verifyNoInteractions(wishDataRepository);
        verifyNoInteractions(wishToOrderConverter);
    }

    private ApplicationArguments getAA(String filePath) {
        return new ApplicationArguments() {
            @Override
            public String[] getSourceArgs() {
                return new String[0];
            }

            @Override
            public Set<String> getOptionNames() {
                return null;
            }

            @Override
            public boolean containsOption(String name) {
                return true;
            }

            @Override
            public List<String> getOptionValues(String name) {
                return singletonList(filePath);
            }

            @Override
            public List<String> getNonOptionArgs() {
                return null;
            }
        };
    }
}