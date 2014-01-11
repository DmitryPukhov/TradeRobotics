package trading.view.swing;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import org.springframework.context.support.GenericXmlApplicationContext;

import trading.app.history.HistoryProvider;
import trading.app.history.HistoryWriter;
import trading.app.neural.NeuralContext;
import trading.app.neural.NeuralService;
import trading.app.realTime.RealTimeEmulator;
import trading.app.realTime.RealTimeProvider;

/**
 * Main application form
 * 
 * @author dima
 * 
 */
public class NeuralNetworkForm extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Spring context
	 */
	static GenericXmlApplicationContext ctx;

	/**
	 * Main content panel
	 */
	private JPanel contentPane;

	/**
	 * Network settings panel in network tab.
	 */
	private NetworkPanel networkPanel;

	/**
	 * Network training panel in Train tab
	 */
	private LearnPanel learnPanel;

	/**
	 * Network testing panel in Test tab
	 */
	private TestPanel testPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Spring initialization
		ctx = new GenericXmlApplicationContext();
		ctx.load("classpath:META-INF/spring/application-context.xml");
		ctx.registerShutdownHook();
		ctx.refresh();

		// Create and run main application form - this form
		final NeuralNetworkForm frame = ctx.getBean(NeuralNetworkForm.class);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// NeuralNetworkForm frame = new NeuralNetworkForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NeuralNetworkForm(NeuralContext neuralContext,
			NeuralService neuralService, HistoryProvider historyProvider,
			HistoryWriter historyWriter, RealTimeProvider realTimeProvider,
			RealTimeEmulator realTimeEmulator) {
		// This form init
		setTitle("Neural Network  Trading");
		setMinimumSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(1024, 768));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		// Tabbed pane init
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tabbedPane, 0,
				SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, tabbedPane, 0,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tabbedPane, 0,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, tabbedPane, 0,
				SpringLayout.EAST, contentPane);
		contentPane.add(tabbedPane);

		// Add runtime tab
		Level1RuntimePanel runtimePanel = new Level1RuntimePanel(neuralContext,
				historyProvider, historyWriter, realTimeProvider);
		tabbedPane.addTab("Runtime", null, runtimePanel, null);

		// Add network tab
		networkPanel = new NetworkPanel(neuralContext, neuralService);
		networkPanel.updateView();
		networkPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				// Save from ui to context
				networkPanel.updateContext();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// Load from context to UI
				networkPanel.updateView();
			}
		});
		tabbedPane.addTab("Network", null, networkPanel, null);

		// Add learn tab
		learnPanel = new LearnPanel(neuralContext, neuralService,
				historyProvider);
		tabbedPane.addTab("Learn", null, learnPanel, null);

		testPanel = new TestPanel(neuralContext, neuralService);
		tabbedPane.addTab("Test", null, testPanel, null);

	}
}
