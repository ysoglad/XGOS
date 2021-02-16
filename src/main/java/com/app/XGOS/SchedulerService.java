package com.app.XGOS;

import com.app.XGOS.converter.WishToOrderConverter;
import com.app.XGOS.sqlRepository.OrderPrivateData;
import com.app.XGOS.sqlRepository.WishDataRepository;
import com.app.XGOS.wishProvider.FromXmlWishIterativeProvider;
import com.app.XGOS.wishProvider.IWishIterativeProvider;
import com.app.XGOS.wishProvider.WishProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class SchedulerService {

    private final IWishIterativeProvider iWishIterativeProvider;
    private final WishDataRepository wishDataRepository;
    private final WishToOrderConverter wishToOrderConverter;
    private String wishSourceFilePathname;

    @Value("${wish.src.path.param.name}")
    private String wishListArgName;

    @Value("${wish.batch.parse.amount}")
    private int numberOfWishesToParse;

    @Autowired
    public SchedulerService(FromXmlWishIterativeProvider iWishIterativeProvider,
                            WishDataRepository wishDataRepository,
                            WishToOrderConverter wishToOrderConverter) {
        this.iWishIterativeProvider = iWishIterativeProvider;
        this.wishDataRepository = wishDataRepository;
        this.wishToOrderConverter = wishToOrderConverter;

    }

    //TODO figure out how does Santa wants to provide location of the file
    @Autowired
    public void setWishSourceFilePathname(ApplicationArguments args) {
        if (args.containsOption(wishListArgName)) {
            wishSourceFilePathname = args.getOptionValues(wishListArgName).get(0);
        } else {
            System.err.println("'wishSourceFilePathname' parameter not provided. Please ReadMe!");
        }
    }


    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    void updateWishesAndOrdersDatabase() {
        File inputWishFile = new File(wishSourceFilePathname);
        if (!inputWishFile.isFile()) {
            System.out.println("PSEUDO LOG: file not present at location '" + wishSourceFilePathname + "' at time XYZ");
            return;
        }
        parseAndUpdate(inputWishFile.toURI());
    }

    private void parseAndUpdate(URI uri) {
        try {
            iWishIterativeProvider.initFromNewSource(uri);
            while (iWishIterativeProvider.isMoreDataAvailable()) {
                saveWishedToDb(iWishIterativeProvider.getUpToNWishes(numberOfWishesToParse)
                        .stream()
                        .map(wishToOrderConverter::getOrder)
                        .collect(Collectors.toList()));
            }
        } catch (WishProviderException wishProviderException) {
            System.out.println("PSEUDO LOG: wishProviderException" + wishProviderException.getMessage());
        }
    }

    @Transactional
    private void saveWishedToDb(Collection<OrderPrivateData> opdCollection) {
        wishDataRepository.saveAll(opdCollection);
    }
}