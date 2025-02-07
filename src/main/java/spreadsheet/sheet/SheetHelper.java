// SPDX-License-Identifier: MIT
package spreadsheet.sheet;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XMultiPropertySet;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNamed;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XCellRangeAddressable;
import com.sun.star.sheet.XCellRangeFormula;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XUsedAreaCursor;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.table.XCellRange;
import com.sun.star.table.XColumnRowRange;
import com.sun.star.table.XTableColumns;
import com.sun.star.uno.UnoRuntime;

public class SheetHelper {

    private static final String[] EMPTY_STRING_ARRAY = new String[] {};

    private List<SortedMap<String, Object>> columnProperties = Collections.emptyList();
    private SortedMap<String, Object> headerProperties = Collections.emptySortedMap();
    private String[][] sheetFormulas;
    private String sheetName;

    public void setHeaderProperties(final SortedMap<String, Object> properties) {
        this.headerProperties = properties;
    }

    public void setColumnProperties(final List<SortedMap<String, Object>> properties) {
        this.columnProperties = properties;
    }

    public void setSheetFormulas(final String[][] formulas) {
        this.sheetFormulas = formulas;
    }

    public void setSheetName(final String name) {
        this.sheetName = name;
    }

    public static XTableColumns getUsedColumns(final XSpreadsheet sheet) {
        final XUsedAreaCursor usedAreaCursor = UnoRuntime.queryInterface(XUsedAreaCursor.class, sheet.createCursor());
        usedAreaCursor.gotoStartOfUsedArea(false);
        usedAreaCursor.gotoEndOfUsedArea(true);
        return UnoRuntime.queryInterface(XColumnRowRange.class, usedAreaCursor).getColumns();
    }

    private void setSheetName(final XSpreadsheet sheet) {
        final XNamed xNamed = UnoRuntime.queryInterface(XNamed.class, sheet);
        xNamed.setName(sheetName());
    }

    private void setHeaderProperties(final XSpreadsheet sheet) throws IndexOutOfBoundsException, IllegalArgumentException, PropertyVetoException, WrappedTargetException {
        final XCellRange cellRange = sheet.getCellRangeByPosition(0, 0, columnProperties().size() - 1, 0);
        final String[] keys = headerProperties().keySet().toArray(EMPTY_STRING_ARRAY);
        final Object[] values = headerProperties().values().toArray();
        UnoRuntime.queryInterface(XMultiPropertySet.class, cellRange).setPropertyValues(keys, values);
    }

    private void setColumnProperties(final XSpreadsheet sheet) throws IndexOutOfBoundsException, WrappedTargetException, IllegalArgumentException, PropertyVetoException {
        XTableColumns columns = UnoRuntime.queryInterface(XColumnRowRange.class, sheet).getColumns();
        int i = 0;
        String[] keys;
        Object[] values;
        for (SortedMap<String, Object> propertyCollection : columnProperties()) {
            keys = propertyCollection.keySet().toArray(EMPTY_STRING_ARRAY);
            values = propertyCollection.values().toArray();
            UnoRuntime.queryInterface(XMultiPropertySet.class, columns.getByIndex(i)).setPropertyValues(keys, values);
            i++;
        }
    }

    public static void setNumberFormatOfColumn(final XSpreadsheet sheet, final int column, final Integer format) throws IndexOutOfBoundsException, WrappedTargetException, IllegalArgumentException, UnknownPropertyException, PropertyVetoException {
        final XTableColumns columns = UnoRuntime.queryInterface(XColumnRowRange.class, sheet).getColumns();
        UnoRuntime.queryInterface(XPropertySet.class, columns.getByIndex(column)).setPropertyValue("NumberFormat", format);
    }

    public void setOptimalWidth(final XTableColumns columns) throws IllegalArgumentException, UnknownPropertyException, PropertyVetoException, WrappedTargetException, IndexOutOfBoundsException {
        final int count = columns.getCount();
        if (columnProperties().isEmpty()) {
            for (int index = 0; index < count; index++) {
                UnoRuntime.queryInterface(XPropertySet.class, columns.getByIndex(index)).setPropertyValue("OptimalWidth", Boolean.TRUE);
            }
        } else {
            for (int index = 0; index < count; index++) {
                if (((Boolean) columnProperties().get(index).getOrDefault("IsVisible", Boolean.TRUE)).booleanValue()) {
                    UnoRuntime.queryInterface(XPropertySet.class, columns.getByIndex(index)).setPropertyValue("OptimalWidth", Boolean.TRUE);
                }
            }
        }
    }

    public static CellRangeAddress getCellRangeAddressOfUsedArea(final XSpreadsheet sheet) {
        final XUsedAreaCursor usedAreaCursor = UnoRuntime.queryInterface(XUsedAreaCursor.class, sheet.createCursor());
        usedAreaCursor.gotoStartOfUsedArea(false);
        usedAreaCursor.gotoEndOfUsedArea(true);
        return UnoRuntime.queryInterface(XCellRangeAddressable.class, usedAreaCursor).getRangeAddress();
    }

    public void updateSheet(final XSpreadsheet sheet) throws com.sun.star.uno.Exception {
        if (sheetName() != null) {
            setSheetName(sheet);
        }
        if (!columnProperties().isEmpty()) {
            setColumnProperties(sheet);
        }
        if (!headerProperties().isEmpty()) {
            setHeaderProperties(sheet);
        }
        if (sheetFormulas() != null) {
            setFormulas(sheet);
        }
        setOptimalWidth(getUsedColumns(sheet));
    }

    public static void setFormula(final XSpreadsheet sheet, final String cellName, final String formula) throws IndexOutOfBoundsException {
        sheet.getCellRangeByName(cellName).getCellByPosition(0, 0).setFormula(formula);
    }

    private void setFormulas(final XSpreadsheet sheet) throws IndexOutOfBoundsException {
        final XCellRange cellRange = sheet.getCellRangeByPosition(0, 0, sheetFormulas()[0].length - 1, sheetFormulas().length - 1);
        UnoRuntime.queryInterface(XCellRangeFormula.class, cellRange).setFormulaArray(sheetFormulas());
    }

    private List<SortedMap<String, Object>> columnProperties() {
        return this.columnProperties;
    }

    private SortedMap<String, Object> headerProperties() {
        return this.headerProperties;
    }

    private String[][] sheetFormulas() {
        return this.sheetFormulas;
    }

    private String sheetName() {
        return this.sheetName;
    }
}
