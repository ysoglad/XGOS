package com.app.XGOS.wishProvider;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FromXmlWishIterativeProviderTest {
    @TempDir
    static Path sharedTempDir;
    private static final String correctFile = "wishes.xml";
    private static final String wrongFile = "wrongData.xml";
    private FromXmlWishIterativeProvider fromXmlWishIterativeProvider;

    @BeforeEach
    void setUp() {
        fromXmlWishIterativeProvider = new FromXmlWishIterativeProvider();

    }

    @AfterEach
    void tearDown() throws WishProviderException {
        fromXmlWishIterativeProvider.finish();
    }

    @BeforeAll
    static void setUpAll() throws IOException {
        Path filePath = sharedTempDir.resolve(correctFile);
        Files.write(filePath, String.valueOf(createXmlStructure()).getBytes());
        Path filePath2 = sharedTempDir.resolve(wrongFile);
        Files.write(filePath2, "definitelyNotXmlStructure".getBytes());
    }

    @Test
    void givenWrongUri_whenInitFromNewSource_thenFail() {
        Assertions.assertThrows(WishProviderException.class, () -> {
            fromXmlWishIterativeProvider.initFromNewSource(new URI("anythingButFileUri"));
        });
    }

    @Test
    void givenData_whenIsMoreDataAvailable_thenReturn() throws WishProviderException {
        fromXmlWishIterativeProvider.initFromNewSource(sharedTempDir.resolve(correctFile).toUri());
        Assertions.assertTrue(fromXmlWishIterativeProvider.isMoreDataAvailable());
    }

    @Test
    void givenNone_whenIsMoreDataAvailable_thenReturn() throws WishProviderException {
        fromXmlWishIterativeProvider.initFromNewSource(sharedTempDir.resolve(correctFile).toUri());
        fromXmlWishIterativeProvider.getUpToNWishes(2);
        Assertions.assertFalse(fromXmlWishIterativeProvider.isMoreDataAvailable());
    }

    @Test
    void givenNotInitialized_whenIsMoreDataAvailable_thenReturn() {
        Assertions.assertFalse(fromXmlWishIterativeProvider.isMoreDataAvailable());
    }

    @Test
    void givenWrongArgument_whenGetUpToNWishes_thenThrow() {
        Assertions.assertThrows(WishProviderException.class,
                () -> fromXmlWishIterativeProvider.getUpToNWishes(0));

        Assertions.assertThrows(WishProviderException.class,
                () -> fromXmlWishIterativeProvider.getUpToNWishes(-1));
    }

    @Test
    void givenNoInit_whenGetUpToNWishes_thenThrow() {
        Assertions.assertThrows(WishProviderException.class,
                () -> fromXmlWishIterativeProvider.getUpToNWishes(-1));
    }

    @Test
    void givenFinished_whenGetUpToNWishes_thenThrow() throws WishProviderException {
        fromXmlWishIterativeProvider.initFromNewSource(sharedTempDir.resolve(correctFile).toUri());
        fromXmlWishIterativeProvider.finish();
        Assertions.assertThrows(WishProviderException.class,
                () -> fromXmlWishIterativeProvider.getUpToNWishes(-1));
    }

    @Test
    void givenCorruptedXml_whenGetUpToNWishes_thenThrow() throws WishProviderException {
        fromXmlWishIterativeProvider.initFromNewSource(sharedTempDir.resolve(wrongFile).toUri());
        Assertions.assertThrows(WishProviderException.class,
                () -> fromXmlWishIterativeProvider.getUpToNWishes(-1));
    }

    @Test
    void givenTooBigArg_whenGetUpToNWishes_returnMax() throws WishProviderException {
        fromXmlWishIterativeProvider.initFromNewSource(sharedTempDir.resolve(correctFile).toUri());
        Assertions.assertEquals(2, fromXmlWishIterativeProvider.getUpToNWishes(3).size());
    }

    @Test
    void givenNotMax_whenGetUpToNWishes_returnNotMax() throws WishProviderException {
        fromXmlWishIterativeProvider.initFromNewSource(sharedTempDir.resolve(correctFile).toUri());
        Assertions.assertEquals(1, fromXmlWishIterativeProvider.getUpToNWishes(1).size());
    }

    @Test
    void givenNotMax_whenGetUpToNWishes_returnNotMaxIterative() throws WishProviderException {
        fromXmlWishIterativeProvider.initFromNewSource(sharedTempDir.resolve(correctFile).toUri());
        Collection<Wish> wc = new ArrayList<>();
        while (fromXmlWishIterativeProvider.isMoreDataAvailable()) {
            wc.addAll(fromXmlWishIterativeProvider.getUpToNWishes(1));
        }
        Assertions.assertEquals(2, wc.size());
    }

    @Test
    void givenData_whenGetUpToNWishes_thenCheckCorrect() throws WishProviderException {
        fromXmlWishIterativeProvider.initFromNewSource(sharedTempDir.resolve(correctFile).toUri());
        List<Wish> wc = fromXmlWishIterativeProvider.getUpToNWishes(2);

        Assertions.assertEquals(data[0], wc.get(0).getChild().getName());
        Assertions.assertEquals(data[1], wc.get(0).getChild().getSurname());
        Assertions.assertEquals(data[2], wc.get(0).getText());
        Assertions.assertEquals(data[3], wc.get(0).getDate());
        Assertions.assertEquals(data[4], wc.get(1).getChild().getName());
        Assertions.assertEquals(data[5], wc.get(1).getChild().getSurname());
        Assertions.assertEquals(data[6], wc.get(1).getText());
        Assertions.assertEquals(data[7], wc.get(1).getDate());
    }

    final static String[] data = new String[]{
            "name1",
            "surname1",
            "text1",
            "01/01/0001",
            "name2",
            "surname2",
            "text2",
            "02/02/0002",

    };

    private static StringBuilder createXmlStructure() {
        return new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<wishes>")
                .append("<wish>")
                .append("<child>")
                .append("<name>" + data[0] + "</name>")
                .append("<surname>" + data[1] + "</surname>")
                .append("</child>")
                .append("<text>" + data[2] + "</text>")
                .append("<datetime>" + data[3] + "</datetime>")
                .append("</wish>")
                .append("<wish>")
                .append("<child>")
                .append("<name>" + data[4] + "</name>")
                .append("<surname>" + data[5] + "</surname>")
                .append("</child>")
                .append("<text>" + data[6] + "</text>")
                .append("<datetime>" + data[7] + "</datetime>")
                .append("</wish>")
                .append("</wishes>");
    }
}
