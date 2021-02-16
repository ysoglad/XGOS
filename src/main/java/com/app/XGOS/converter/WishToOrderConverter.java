package com.app.XGOS.converter;

import com.app.XGOS.parser.DateParser;
import com.app.XGOS.parser.IDateParser;
import com.app.XGOS.sqlRepository.OrderPrivateData;
import com.app.XGOS.sqlRepository.OrderPublicData;
import com.app.XGOS.wishProvider.Wish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WishToOrderConverter {
    private IDateParser iDateParser;

    @Autowired
    public void setDateParser(DateParser dateParser) {
        this.iDateParser = dateParser;
    }

    public OrderPrivateData getOrder(Wish wish) {
        OrderPrivateData orderPrivateData = new OrderPrivateData(
                wish.getChild().getName(),
                wish.getChild().getSurname(),
                wish.getText());
        LocalDate orderDate = iDateParser.adjustDate(iDateParser.parseDate(wish.getDate()));
        OrderPublicData orderPublicData = new OrderPublicData(orderDate);
        orderPrivateData.setOrderPublicData(orderPublicData);
        orderPublicData.setOrderPrivateData(orderPrivateData);
        return orderPrivateData;
    }
}
