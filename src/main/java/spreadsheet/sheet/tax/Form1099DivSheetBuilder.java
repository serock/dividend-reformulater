// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.sheet.XSpreadsheet;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;

public class Form1099DivSheetBuilder extends SheetBuilder {

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet form1099DivSheet = SpreadsheetDocumentHelper.addSheet(document(), "form-1099-div");
        sheetHelper().setColumnProperties(createColumnProperties());
        sheetHelper().updateSheet(form1099DivSheet);
    }

    private List<SortedMap<String, Object>> createColumnProperties() {
        final List<SortedMap<String, Object>> columnProperties = new ArrayList<>(3);
        addBoxColumnProperties(columnProperties);
        addDescriptionColumnProperties(columnProperties);
        addAmountColumnProperties(columnProperties);
        return columnProperties;
    }

    private void addBoxColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addDescriptionColumnProperties(List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addAmountColumnProperties(List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }
}
