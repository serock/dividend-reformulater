// SPDX-License-Identifier: MIT
package app;

import java.io.File;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.sheet.XSpreadsheetDocument;

import pdf.PDFHelper;
import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.tax.DividendDetailSheetBuilder;
import spreadsheet.sheet.tax.ForeignTaxPaidSheetBuilder;
import spreadsheet.sheet.tax.Form1099DivSheetBuilder;
import spreadsheet.sheet.tax.NondividendDistributionsSheetBuilder;
import spreadsheet.sheet.tax.OrdinaryDividendsSheetBuilder;
import spreadsheet.sheet.tax.SupplementalInfoSheetBuilder;
import spreadsheet.sheet.tax.OrdinarySourcesSheetBuilder;
import spreadsheet.sheet.tax.TaxExemptDividendsSheetBuilder;
import spreadsheet.sheet.tax.TaxExemptStatesSheetBuilder;
import text.Context;

public class DividendReformulater implements Consumer<String>, Runnable {

    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("app.debug", "false"));

    private final Context context = new Context();

    private File taxPDFFile;

    public static void main(final String[] args) {
        checkClassPath();
        if (args.length == 1) {
            final DividendReformulater app = new DividendReformulater();
            app.taxPDFFile = new File(args[0]);
            app.run();
        } else {
            showUsage();
        }
        System.exit(0);
    }

    private static void checkClassPath() {
        try {
            Class.forName("com.sun.star.comp.helper.Bootstrap");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void accept(final String text) {
        if (DEBUG) {
            System.out.println(text);
        }
        context().state().accept(context(), text);
    }

    private void buildDividendDetailSheet(final XSpreadsheetDocument document) throws IllegalArgumentException, com.sun.star.uno.Exception {
        final DividendDetailSheetBuilder builder = new DividendDetailSheetBuilder();
        builder.setDocument(document);
        builder.setSheetFormulas(context().getDividendDetailFormulas());
        builder.build();
    }

    private void buildForeignTaxPaidSheet(final XSpreadsheetDocument document) throws com.sun.star.uno.Exception {
        final ForeignTaxPaidSheetBuilder builder = new ForeignTaxPaidSheetBuilder();
        builder.setDocument(document);
        builder.setTransactionTypes(context().getForeignTaxTransactionTypes());
        builder.build();
    }

    private void buildForm1099DivSheet(final XSpreadsheetDocument document) throws com.sun.star.uno.Exception {
        final Form1099DivSheetBuilder builder = new Form1099DivSheetBuilder();
        builder.setDocument(document);
        builder.setSheetFormulas(context().getForm1099DivFormulas());
        builder.build();
    }

    private static void buildNondividendDistributionsSheet(final XSpreadsheetDocument document) throws com.sun.star.uno.Exception {
        final NondividendDistributionsSheetBuilder builder = new NondividendDistributionsSheetBuilder();
        builder.setDocument(document);
        builder.build();
    }

    private static void buildOrdinaryDividendsSheet(final XSpreadsheetDocument document) throws IllegalArgumentException, com.sun.star.uno.Exception {
        final OrdinaryDividendsSheetBuilder builder = new OrdinaryDividendsSheetBuilder();
        builder.setDocument(document);
        builder.build();
    }

    private static void buildOrdinarySourcesSheet(final XSpreadsheetDocument document) throws com.sun.star.uno.Exception {
        final OrdinarySourcesSheetBuilder builder = new OrdinarySourcesSheetBuilder();
        builder.setDocument(document);
        builder.build();
    }

    private void buildSupplementalInfoSheet(final XSpreadsheetDocument document) throws com.sun.star.uno.Exception {
        final SupplementalInfoSheetBuilder builder = new SupplementalInfoSheetBuilder();
        builder.setDocument(document);
        builder.setSheetFormulas(context().getSupplementalInfoFormulas());
        builder.build();
    }

    private static void buildTaxExemptDividendsSheet(final XSpreadsheetDocument document) throws com.sun.star.uno.Exception {
        final TaxExemptDividendsSheetBuilder builder = new TaxExemptDividendsSheetBuilder();
        builder.setDocument(document);
        builder.build();
    }

    private static void buildTaxExemptStatesSheet(final XSpreadsheetDocument document) throws com.sun.star.uno.Exception {
        final TaxExemptStatesSheetBuilder builder = new TaxExemptStatesSheetBuilder();
        builder.setDocument(document);
        builder.build();
    }

    @Override
    public void run() {
        try {
            final PDFHelper pdfHelper = new PDFHelper();
            final Stream<String> lines = pdfHelper.getTextLines(this.taxPDFFile);
            lines.forEachOrdered(this);
            final SpreadsheetDocumentHelper docHelper = new SpreadsheetDocumentHelper();
            final XSpreadsheetDocument document = docHelper.createDocument();
            if (pdfHelper.isForm1099()) {
                buildDividendDetailSheet(document);
                buildOrdinaryDividendsSheet(document);
                if (context().hasNondividendDistribution()) {
                    buildNondividendDistributionsSheet(document);
                }
                if (context().hasTaxExemptDividend()) {
                    buildTaxExemptDividendsSheet(document);
                }
                if (context().hasForeignTaxPaid()) {
                    buildForeignTaxPaidSheet(document);
                }
                if (context().hasSupplementalInfo()) {
                    buildSupplementalInfoSheet(document);
                    buildOrdinarySourcesSheet(document);
                }
                if (context().hasTaxExemptDividend()) {
                    buildTaxExemptStatesSheet(document);
                }
                buildForm1099DivSheet(document);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void showUsage() {
        System.out.println("Usage: java -jar dividend-reformulater.jar <consolidated-1099.pdf>");
    }

    private Context context() {
        return this.context;
    }
}
