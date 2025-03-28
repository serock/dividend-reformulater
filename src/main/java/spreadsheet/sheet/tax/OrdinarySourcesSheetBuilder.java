// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
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

    private static final TableFilterField[] filterFields = createFilterFields();

    public OrdinarySourcesSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet ordinarySourcesSheet = SpreadsheetDocumentHelper.addSheet(document(), "ordinary-by-source");
        final CellAddress cellAddress = SheetHelper.getCellAddress(ordinarySourcesSheet, 0, 0);

        pivotTableHelper().initialize(ordinarySourcesSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.SI_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.SI_FIELD_SOURCE);
        pivotTableHelper().setDataOrientation(Constants.SI_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.SI_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(filterFields);
        pivotTableHelper().setSortInfo(Constants.SI_FIELD_SOURCE, new String[] {"Fed Source Total", "Fgn Source Inc Tot", "Fgn Source Inc Qual", "Fgn Source Inc Adj"});
        pivotTableHelper().showTotalsColumn(false);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("ordinary-by-source", cellAddress);

        sheetHelper().updateSheet(ordinarySourcesSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), ordinarySourcesSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 2);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet supplementalInfoSheet = SpreadsheetDocumentHelper.getSheet(document(), "supplemental-info");
        return SheetHelper.getCellRangeAddressOfUsedArea(supplementalInfoSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final List<TableFilterField> fields = new ArrayList<>(1);

        TableFilterField field;

        field = new TableFilterField();
        field.Field = Constants.SI_FIELD_SOURCE;
        field.IsNumeric = false;
        field.Operator = FilterOperator.NOT_EMPTY;
        fields.add(field);

        return fields.toArray(new TableFilterField[0]);
    }
}
