package com.app.XGOS.converter;

import com.app.XGOS.parser.DateParser;
import com.app.XGOS.sqlRepository.OrderPrivateData;
import com.app.XGOS.wishProvider.Wish;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class WishToOrderConverterTest {
    @MockBean
    DateParser dateParser;

    @Autowired
    WishToOrderConverter wishToOrderConverter;

    @Test
    void givenWish_whenGetOrder_thanSuccess() {
        Wish wish = new Wish();
        String[] strings = new String[]{
                "testDate",
                "testText",
                "testName",
                "testSurname"
        };
        wish.setDate(strings[0]);
        wish.setText(strings[1]);
        wish.getChild().setName(strings[2]);
        wish.getChild().setSurname(strings[3]);

        LocalDate ld = LocalDate.now();
        Mockito.when(dateParser.parseDate(strings[0])).thenReturn(ld);
        Mockito.when(dateParser.adjustDate(ld)).thenReturn(ld);
        OrderPrivateData opd = wishToOrderConverter.getOrder(wish);

        verify(dateParser, times(1)).parseDate(any());
        verify(dateParser, times(1)).adjustDate(any());
        Assertions.assertEquals(opd.getOrderPublicData().getDate().toString(), ld.toString());
        Assertions.assertEquals(opd.getText(), strings[1]);
        Assertions.assertEquals(opd.getChildName(), strings[2]);
        Assertions.assertEquals(opd.getChildSurname(), strings[3]);
        Assertions.assertEquals(opd.getOrderPublicData().getOrderPrivateData(), opd);
    }
}