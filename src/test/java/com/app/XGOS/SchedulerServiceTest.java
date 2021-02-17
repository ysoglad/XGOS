package com.app.XGOS;

import com.app.XGOS.converter.WishToOrderConverter;
import com.app.XGOS.sqlRepository.OrderPrivateData;
import com.app.XGOS.sqlRepository.WishDataRepository;
import com.app.XGOS.wishProvider.FromXmlWishIterativeProvider;
import com.app.XGOS.wishProvider.Wish;
import com.app.XGOS.wishProvider.WishProviderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void givenRightInput_whenUpdateWishesAndOrdersDatabase_thenCheckInteractions() throws WishProviderException {
        Mockito.when(fromXmlWishIterativeProvider.isMoreDataAvailable())
                .thenReturn(true, true, false);
        Mockito.when(fromXmlWishIterativeProvider.getUpToNWishes(anyInt()))
                .thenReturn(singletonList(new Wish()), singletonList(new Wish()));
        Mockito.when(wishToOrderConverter.getOrder(any()))
                .thenReturn(new OrderPrivateData(), new OrderPrivateData());

        schedulerService.updateWishesAndOrdersDatabase();

        verify(fromXmlWishIterativeProvider, times(1)).initFromNewSource(any());
        verify(fromXmlWishIterativeProvider, times(3)).isMoreDataAvailable();
        verify(fromXmlWishIterativeProvider, times(2)).getUpToNWishes(anyInt());
        verify(wishToOrderConverter, times(2)).getOrder(any());
        verify(wishDataRepository, times(2)).saveAll(any());
    }

    @Test
    void givenInsufficientWishInput_whenUpdateWishesAndOrdersDatabase_thenCheckInteractions() throws WishProviderException {
        Mockito.when(fromXmlWishIterativeProvider.isMoreDataAvailable())
                .thenReturn(true, false);
        Mockito.when(fromXmlWishIterativeProvider.getUpToNWishes(anyInt()))
                .thenReturn(new ArrayList<>());

        schedulerService.updateWishesAndOrdersDatabase();

        verify(fromXmlWishIterativeProvider, times(1)).initFromNewSource(any());
        verify(fromXmlWishIterativeProvider, times(2)).isMoreDataAvailable();
        verify(fromXmlWishIterativeProvider, times(1)).getUpToNWishes(anyInt());
        verify(wishToOrderConverter, times(0)).getOrder(any());
        verify(wishDataRepository, times(0)).saveAll(any());
    }

    @Test
    void givenInsufficientOrderInput_whenUpdateWishesAndOrdersDatabase_thenCheckInteractions() throws WishProviderException {
        Mockito.when(fromXmlWishIterativeProvider.isMoreDataAvailable())
                .thenReturn(true, false);
        Mockito.when(fromXmlWishIterativeProvider.getUpToNWishes(anyInt()))
                .thenReturn(singletonList(new Wish()));

        schedulerService.updateWishesAndOrdersDatabase();

        verify(fromXmlWishIterativeProvider, times(1)).initFromNewSource(any());
        verify(fromXmlWishIterativeProvider, times(2)).isMoreDataAvailable();
        verify(fromXmlWishIterativeProvider, times(1)).getUpToNWishes(anyInt());
        verify(wishToOrderConverter, times(1)).getOrder(any());
        verify(wishDataRepository, times(0)).saveAll(any());
    }

    @Test
    void givenWrongInput_whenUpdateWishesAndOrdersDatabase_thenNoInteractions() throws WishProviderException {
        Mockito.doThrow(new WishProviderException("")).when(fromXmlWishIterativeProvider).initFromNewSource(any());
        schedulerService.updateWishesAndOrdersDatabase();

        verify(fromXmlWishIterativeProvider, times(1)).initFromNewSource(any());
        verify(fromXmlWishIterativeProvider, times(0)).isMoreDataAvailable();
        verify(fromXmlWishIterativeProvider, times(0)).getUpToNWishes(anyInt());
        verify(fromXmlWishIterativeProvider, times(0)).finish();
        verifyNoInteractions(wishDataRepository);
        verifyNoInteractions(wishToOrderConverter);
    }

    @Test
    void givenWrongInput_whenSetWishSourceFilePathname_thenNullValue() throws WishProviderException {
        ArgumentCaptor<URI> argument = ArgumentCaptor.forClass(URI.class);

        Mockito.doThrow(new WishProviderException("")).when(fromXmlWishIterativeProvider).initFromNewSource(any());
        schedulerService.setWishSourceFilePathname(getAA("\\\\whatever"));
        schedulerService.updateWishesAndOrdersDatabase();

        verify(fromXmlWishIterativeProvider).initFromNewSource(argument.capture());
        assertNull(argument.getValue());
    }

    @Test
    void givenRightInput_whenSetWishSourceFilePathname_thenCorrectValue(@TempDir Path tempDir) throws WishProviderException {
        Path path = tempDir.resolve("example.xml");
        ArgumentCaptor<URI> argument = ArgumentCaptor.forClass(URI.class);

        Mockito.doThrow(new WishProviderException("")).when(fromXmlWishIterativeProvider).initFromNewSource(any());
        schedulerService.setWishSourceFilePathname(getAA(path.toString()));
        schedulerService.updateWishesAndOrdersDatabase();

        verify(fromXmlWishIterativeProvider).initFromNewSource(argument.capture());
        assertEquals(path.toUri(), argument.getValue());
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