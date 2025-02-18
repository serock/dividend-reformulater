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
import text.SupplementalInfoState;

public class IncomeSourcesSheetBuilder extends PivotTableSheetBuilder {

    public IncomeSourcesSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet supplementalSummarySheet = SpreadsheetDocumentHelper.addSheet(document(), "income-sources");
        final CellAddress cellAddress = SheetHelper.getCellAddress(supplementalSummarySheet, 0, 0);

        pivotTableHelper().setTablesSupplier(supplementalSummarySheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(SupplementalInfoState.FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(SupplementalInfoState.FIELD_SOURCE);
        pivotTableHelper().setDataOrientation(SupplementalInfoState.FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(SupplementalInfoState.FIELD_AMOUNT);
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
