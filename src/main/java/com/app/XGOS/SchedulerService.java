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
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SchedulerService {

    private final IWishIterativeProvider iWishIterativeProvider;
    private final WishDataRepository wishDataRepository;
    private final WishToOrderConverter wishToOrderConverter;
    private URI wishFileUri;

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
            String wishSourceFilePathname = args.getOptionValues(wishListArgName).get(0);
            try {
                wishFileUri = Paths.get(wishSourceFilePathname).toAbsolutePath().toUri();
            } catch (java.nio.file.InvalidPathException e) {
                System.err.println("Invalid path provided. Please ReadMe!");
            }
        } else {
            System.err.println("'wishSourceFilePathname' parameter not provided. Please ReadMe!");
        }
    }

    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    void updateWishesAndOrdersDatabase() {
        try {
            iWishIterativeProvider.initFromNewSource(wishFileUri);
            while (iWishIterativeProvider.isMoreDataAvailable()) {
                attemptSaveWishesToDb(iWishIterativeProvider.getUpToNWishes(numberOfWishesToParse)
                        .stream()
                        .map(wishToOrderConverter::getOrder)
                        .collect(Collectors.toList()));
            }
        } catch (WishProviderException wishProviderException) {
            //TODO provide exception handler
            System.out.println("PSEUDO LOG: wishProviderException" + wishProviderException.getMessage());
        }
    }

    @Transactional
    private void saveWishesToDb(Collection<OrderPrivateData> opdCollection) {
        wishDataRepository.saveAll(opdCollection);
    }

    private void attemptSaveWishesToDb(Collection<OrderPrivateData> opdCollection) {
        opdCollection.removeIf(Objects::isNull);
        if (null != opdCollection && !opdCollection.isEmpty())
            saveWishesToDb(opdCollection);
    }
}