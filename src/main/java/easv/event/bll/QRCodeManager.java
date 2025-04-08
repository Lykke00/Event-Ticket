package easv.event.bll;

import com.spire.barcode.BarCodeGenerator;
import com.spire.barcode.BarCodeType;
import com.spire.barcode.BarcodeSettings;
import com.spire.barcode.QRCodeECL;
import easv.event.be.TicketEvent;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class QRCodeManager {

    public static void generateQrCode(TicketEvent ticketEvent, UUID uuid, String email, int amount) throws IOException {
        BarcodeSettings settings = getBarcodeSettings(ticketEvent, uuid, amount);

        BarCodeGenerator barCodeGenerator = new BarCodeGenerator(settings);
        BufferedImage qrImage = barCodeGenerator.generateImage();

        File pdfFile = new File(uuid.toString() + ".pdf");
        saveQrCodeToFile(qrImage, ticketEvent, amount, email, pdfFile);

        if (Desktop.isDesktopSupported())
            Desktop.getDesktop().open(pdfFile);
    }

    private static void saveQrCodeToFile(BufferedImage qrImage, TicketEvent ticketEvent, int amount, String email, File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDImageXObject pdImage = LosslessFactory.createFromImage(document, qrImage);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.drawImage(pdImage, 100, 500);
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 14);
        contentStream.newLineAtOffset(100, 480);

        double totalPrice = ticketEvent.getPrice() * amount;
        contentStream.showText(ticketEvent.getTicket().getName() + " " + amount + "x billetter til " + ticketEvent.getEvent().getTitle() + " " + Double.toString(totalPrice) + " kr." + " " + email);
        contentStream.endText();

        contentStream.close();

        document.save(file);
        document.close();
    }

    private static BarcodeSettings getBarcodeSettings(TicketEvent ticketEvent, UUID uuid, int amount) {
        BarcodeSettings settings = new BarcodeSettings();
        settings.setType(BarCodeType.QR_Code);
        String data = uuid.toString();
        settings.setData(data);

        settings.setX(2);
        settings.setQRCodeECL(QRCodeECL.M);

        settings.setTopText(ticketEvent.getEvent().getTitle());
        settings.setBottomText(amount + "x " + ticketEvent.getTicket().getName());

        settings.hasBorder(false);

        settings.setShowText(false);
        settings.setShowTopText(true);
        settings.setShowBottomText(true);
        return settings;
    }
}
