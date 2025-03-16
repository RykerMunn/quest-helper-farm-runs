package com.questhelper.panel;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import net.runelite.client.util.Text;


public class MultiCheckBoxCombo<T> extends JPanel {
    private JButton comboButton;
    private JPopupMenu popupMenu;
    private JList<T> list;
    private DefaultListModel<T> listModel;
    private List<T> selectedItems = new ArrayList<>();
    private List<ListSelectionListener> listeners = new ArrayList<>();

    public MultiCheckBoxCombo(List<T> items) {
        setLayout(new BorderLayout());
        comboButton = new JButton("Select...");  // Looks like a combo box button
        add(comboButton, BorderLayout.CENTER);

        // Build the popup with a JList of checkboxes
        popupMenu = new JPopupMenu();
        listModel = new DefaultListModel<>();
        for (T item : items) {
            listModel.addElement(item);
        }
        list = new JList<>(listModel);
        list.setCellRenderer(new CheckBoxListCellRenderer());
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(list);
        popupMenu.add(scrollPane);

        // Show/hide popup when button is clicked
        comboButton.addActionListener(e -> popupMenu.show(this, 0, getHeight()));

        // Listen for list clicks to toggle selection
        list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(javax.swing.event.ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					updateSelectedItems();
                    updateButtonText();

                    for (ListSelectionListener listener : listeners) {
                        listener.valueChanged(e);
                    }
				}
			}
		});
    }

    private void updateSelectedItems() {
        selectedItems.clear();
        selectedItems.addAll(list.getSelectedValuesList());
    }

    private void updateButtonText() {
        if (selectedItems.isEmpty()) {
            comboButton.setText("Select...");
        } else {
            StringBuilder sb = new StringBuilder();
            for (T item : selectedItems) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(item instanceof Enum ? Text.titleCase((Enum)item) : item.toString());
            }
            comboButton.setText(sb.toString());
            comboButton.setToolTipText(sb.toString());
        }
    }

    public List<T> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<T> items) {
        for (int i = 0; i < listModel.getSize(); i++) {
            T item = listModel.getElementAt(i);
            if (items.contains(item)) {
                list.addSelectionInterval(i, i);
            }
        }
        updateSelectedItems();
        updateButtonText();
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        listeners.add(listener);
    }
    
    private class CheckBoxListCellRenderer extends JCheckBox implements ListCellRenderer<T> {
        @Override
        public Component getListCellRendererComponent(JList<? extends T> list,
                                                      T value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            setText(value instanceof Enum ? Text.titleCase((Enum)value) :  value.toString());
            setSelected(isSelected);
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            return this;
        }
    }
}