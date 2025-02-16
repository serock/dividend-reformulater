// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;

public class SupplementalSummarySheetBuilder extends PivotTableSheetBuilder {

    public SupplementalSummarySheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet supplementalSummarySheet = SpreadsheetDocumentHelper.addSheet(document(), "supplemental-summary");
        final CellAddress cellAddress = SheetHelper.getCellAddress(supplementalSummarySheet, 0, 0);

        pivotTableHelper().setTablesSupplier(supplementalSummarySheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(SupplementalInfoSheetBuilder.FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(SupplementalInfoSheetBuilder.FIELD_SOURCE);
        pivotTableHelper().setDataOrientation(SupplementalInfoSheetBuilder.FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(SupplementalInfoSheetBuilder.FIELD_AMOUNT);
        pivotTableHelper().showTotalsColumn(false);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("supplemental-summary", cellAddress);

        sheetHelper().updateSheet(supplementalSummarySheet);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet supplementalInfoSheet = SpreadsheetDocumentHelper.getSheet(document(), "supplemental-info");
        return SheetHelper.getCellRangeAddressOfUsedArea(supplementalInfoSheet);
    }
}
