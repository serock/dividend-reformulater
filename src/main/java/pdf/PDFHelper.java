// SPDX-License-Identifier: MIT
package pdf;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFHelper {

    private boolean form1099;

    public Stream<String> getTextLines(final File pdfFile) throws IOException {
        final PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text;
        try (final PDDocument doc = Loader.loadPDF(pdfFile)) {
            if (doc.getDocumentInformation().getTitle().equals("1099")) {
                this.form1099 = true;
                pdfTextStripper.setSortByPosition(true);
            }
            text = pdfTextStripper.getText(doc);
        }
        return text.lines();
    }

    public boolean isForm1099() {
        return this.form1099;
    }
}
