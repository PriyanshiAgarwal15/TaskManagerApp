import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TaskManagerApp extends JFrame {

    private JTextField descriptionField;
    private JTextField dueDateField;
    private JComboBox<String> priorityComboBox;
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;

    public TaskManagerApp() {
        setTitle("Task Manager");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        descriptionField = new JTextField(20);
        inputPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        dueDateField = new JTextField(20);
        inputPanel.add(dueDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Priority:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        priorityComboBox = new JComboBox<>(new String[]{"HIGH", "MEDIUM", "LOW"});
        inputPanel.add(priorityComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });
        inputPanel.add(addButton, gbc);

        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTask();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(deleteButton, gbc);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);

        JScrollPane scrollPane = new JScrollPane(taskList);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Start a thread to check for tasks nearing their due dates
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    checkForDueTasks();
                    try {
                        Thread.sleep(60000); // Check every minute
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void addTask() {
        String description = descriptionField.getText();
        String dateStr = dueDateField.getText();
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dateStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Priority priority = Priority.valueOf((String) priorityComboBox.getSelectedItem());

        Task task = new Task(description, dueDate, priority);
        taskListModel.addElement(task);

        // Clear input fields
        descriptionField.setText("");
        dueDateField.setText("");
        priorityComboBox.setSelectedIndex(0);
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            taskListModel.remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Task Manager", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkForDueTasks() {
        for (int i = 0; i < taskListModel.size(); i++) {
            Task task = taskListModel.getElementAt(i);
            long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());
            if (daysUntilDue == 0) {
                JOptionPane.showMessageDialog(this, "Task '" + task.getDescription() + "' is due today!", "Task Due", JOptionPane.INFORMATION_MESSAGE);
            } else if (daysUntilDue == 1) {
                JOptionPane.showMessageDialog(this, "Task '" + task.getDescription() + "' is due tomorrow!", "Task Due", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TaskManagerApp taskManagerApp = new TaskManagerApp();
                taskManagerApp.setVisible(true);
            }
        });
    }
}

class Task {
    private String description;
    private LocalDate dueDate;
    private Priority priority;

    public Task(String description, LocalDate dueDate, Priority priority) {
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "<html><b>Description:</b> " + description + "<br>"
                + "<b>Due Date:</b> " + dueDate + "<br>"
                + "<b>Priority:</b> " + priority + "</html>";
    }
}

enum Priority {
    HIGH,
    MEDIUM,
    LOW
}
