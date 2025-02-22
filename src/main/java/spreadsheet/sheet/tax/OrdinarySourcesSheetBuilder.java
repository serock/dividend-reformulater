// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterConnection;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class OrdinarySourcesSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] tableFilterFields = createFilterFields();

    public OrdinarySourcesSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet ordinarySourcesSheet = SpreadsheetDocumentHelper.addSheet(document(), "ordinary-sources");
        final CellAddress cellAddress = SheetHelper.getCellAddress(ordinarySourcesSheet, 0, 0);

        pivotTableHelper().setTablesSupplier(ordinarySourcesSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.SI_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.SI_FIELD_SOURCE);
        pivotTableHelper().setDataOrientation(Constants.SI_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.SI_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(tableFilterFields);
        pivotTableHelper().showTotalsColumn(false);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("supplemental-summary", cellAddress);

        sheetHelper().updateSheet(ordinarySourcesSheet);
        SpreadsheetDocumentHelper.setActiveSheet(document(), ordinarySourcesSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 2);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet supplementalInfoSheet = SpreadsheetDocumentHelper.getSheet(document(), "supplemental-info");
        return SheetHelper.getCellRangeAddressOfUsedArea(supplementalInfoSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final TableFilterField[] filterFields = new TableFilterField[4];

        filterFields[0] = new TableFilterField();
        filterFields[0].Field = Constants.SI_FIELD_SOURCE;
        filterFields[0].IsNumeric = false;
        filterFields[0].StringValue = "Fed Source Total";
        filterFields[0].Operator = FilterOperator.EQUAL;

        filterFields[1] = new TableFilterField();
        filterFields[1].Connection = FilterConnection.OR;
        filterFields[1].Field = Constants.SI_FIELD_SOURCE;
        filterFields[1].IsNumeric = false;
        filterFields[1].StringValue = "Fgn Source Inc Tot";
        filterFields[1].Operator = FilterOperator.EQUAL;

        filterFields[2] = new TableFilterField();
        filterFields[2].Connection = FilterConnection.OR;
        filterFields[2].Field = Constants.SI_FIELD_SOURCE;
        filterFields[2].IsNumeric = false;
        filterFields[2].StringValue = "Fgn Source Inc Qual";
        filterFields[2].Operator = FilterOperator.EQUAL;

        filterFields[3] = new TableFilterField();
        filterFields[3].Connection = FilterConnection.OR;
        filterFields[3].Field = Constants.SI_FIELD_SOURCE;
        filterFields[3].IsNumeric = false;
        filterFields[3].StringValue = "Fgn Source Inc Adj";
        filterFields[3].Operator = FilterOperator.EQUAL;

        return filterFields;
    }
}
