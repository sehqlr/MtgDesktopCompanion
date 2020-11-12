package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.interfaces.MTGGraders;
import org.magic.gui.renderer.standard.BooleanCellEditorRenderer;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.gui.renderer.standard.IntegerCellEditorRenderer;
import org.magic.services.PluginRegistry;

public class StockTableRenderer implements TableCellRenderer{

	
	Component pane;

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		pane = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		
		if(value instanceof Boolean)
			pane= new BooleanCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		
		if(value instanceof Integer)
			pane= new IntegerCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		
		if(value instanceof Double)
			pane= new DoubleCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
		
		if(value instanceof Map)
		{
			pane = new JPanel();
			((Map<String,Object>)value).entrySet().forEach(e->((JPanel)pane).add(new JLabel(e.getKey() +"("+e.getValue()+")")));
			 
		}
		
		if(value instanceof Grading)
		{
			Grading g = (Grading)value;
			
			try {
			Icon c = PluginRegistry.inst().getPlugin(g.getGraderName(), MTGGraders.class).getIcon();
			pane= new JLabel(g.toString(),c,SwingConstants.LEFT);
			((JLabel)pane).setOpaque(true);
			}
			catch(Exception e)
			{
				pane = new JLabel(g.toString());
				((JLabel)pane).setOpaque(true);
			}
		}
		if (((MagicCardStock) table.getModel().getValueAt(row, 0)).isUpdate()) {
			pane.setBackground(Color.GREEN);
			pane.setForeground(table.getForeground());
		} else if (isSelected) {
			pane.setBackground(table.getSelectionBackground());
			pane.setForeground(table.getSelectionForeground());
		} else {
			pane.setBackground(table.getBackground());
			pane.setForeground(table.getForeground());
		}

		
		
	
		return pane;
	}

}