package com.app.XGOS.wishProvider;

import org.springframework.stereotype.Service;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class FromXmlWishIterativeProvider implements IWishIterativeProvider {
    private XMLEventReader reader;
    private FileInputStream fis;

    @Override
    public void initFromNewSource(URI uri) throws WishProviderException {
        checkIfUriIsAnExistingXmlFile(uri);
        finish();
        try {
            fis = new FileInputStream(new File(uri));
            reader = XMLInputFactory.newInstance().createXMLEventReader(fis);
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new WishProviderException("Unable to init:" + e.getMessage());
        }
    }

    private void checkIfUriIsAnExistingXmlFile(URI uri) throws WishProviderException {
        try {
            if (!new File(uri).isFile()) {
                throw new WishProviderException("File does not exist");
            }
        } catch (IllegalArgumentException iae) {
            throw new WishProviderException("File does not exist");
        }
    }

    @Override
    public List<Wish> getUpToNWishes(int maxNumberToRead) throws WishProviderException {
        if (null == reader) {
            throw new WishProviderException("Reader has not been properly initialized.");
        }
        if (maxNumberToRead <= 0) {
            throw new WishProviderException("maxNumberToRead should be greater than 0.");
        }

        List<Wish> wishes = new ArrayList<>();
        Wish wish = new Wish();
        while (reader.hasNext()) {
            XMLEvent nextEvent;
            try {
                nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "name":
                            nextEvent = reader.nextEvent();
                            wish.getChild().setName(nextEvent.asCharacters().getData());
                            break;
                        case "surname":
                            nextEvent = reader.nextEvent();
                            wish.getChild().setSurname(nextEvent.asCharacters().getData());
                            break;
                        case "text":
                            nextEvent = reader.nextEvent();
                            wish.setText(nextEvent.asCharacters().getData());
                            break;
                        case "datetime":
                            nextEvent = reader.nextEvent();
                            wish.setDate(nextEvent.asCharacters().getData());
                            break;
                    }
                }
            } catch (XMLStreamException e) {
                throw new WishProviderException("Cannot read file" + e.getMessage());
            }
            if (nextEvent.isEndElement()) {
                EndElement endElement = nextEvent.asEndElement();
                if (endElement.getName().getLocalPart().equals("wish")) {
                    wishes.add(wish);
                    wish = new Wish();
                    try {
                        if (reader.hasNext()) {
                            nextEvent = reader.nextEvent();
                            if (nextEvent.isEndElement() &&
                                    nextEvent.asEndElement().getName().getLocalPart().equals("wishes")) {
                                this.finish();
                                break;
                            }
                        }
                    } catch (XMLStreamException e) {
                        throw new WishProviderException("Cannot read file" + e.getMessage());
                    }
                }
            }


            if (wishes.size() == maxNumberToRead) break;
        }
        return wishes;
    }

    @Override
    public boolean isMoreDataAvailable() {
        return null != reader && reader.hasNext();
    }

    @Override
    public void finish() throws WishProviderException {
        try (FileInputStream fisToClose = fis) {
            if (null != reader) {
                XMLEventReader tmp = reader;
                reader = null;
                tmp.close();
            }
        } catch (IOException | XMLStreamException e) {
            throw new WishProviderException("Unable to close resource:" + e.getMessage());
        }
    }
}
