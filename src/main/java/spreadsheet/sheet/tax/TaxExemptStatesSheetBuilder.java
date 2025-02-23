// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.uno.Exception;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class TaxExemptStatesSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] tableFilterFields = createFilterFields();

    public TaxExemptStatesSheetBuilder() {
        super();
    }

    @Override
    public void build() throws Exception {
        final XSpreadsheet taxExemptStatesSheet = SpreadsheetDocumentHelper.addSheet(document(), "tax-exempt-by-state");
        final CellAddress cellAddress = SheetHelper.getCellAddress(taxExemptStatesSheet, 0, 0);

        pivotTableHelper().initialize(taxExemptStatesSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.SI_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.SI_FIELD_STATE);
        pivotTableHelper().setDataOrientation(Constants.SI_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.SI_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(tableFilterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("tax-exempt-by-state", cellAddress);

        sheetHelper().updateSheet(taxExemptStatesSheet);
        SpreadsheetDocumentHelper.setActiveSheet(document(), taxExemptStatesSheet);
        SpreadsheetDocumentHelper.freezeColumnsOfActiveSheet(document(), 2);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet supplementalInfoSheet = SpreadsheetDocumentHelper.getSheet(document(), "supplemental-info");
        return SheetHelper.getCellRangeAddressOfUsedArea(supplementalInfoSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final TableFilterField[] filterFields = new TableFilterField[1];

        filterFields[0] = new TableFilterField();
        filterFields[0].Field = Constants.SI_FIELD_SOURCE;
        filterFields[0].IsNumeric = false;
        filterFields[0].Operator = FilterOperator.EMPTY;

        return filterFields;
    }
}
