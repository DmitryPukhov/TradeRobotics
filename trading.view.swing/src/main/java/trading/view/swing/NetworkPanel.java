package trading.view.swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.encog.neural.networks.BasicNetwork;

import trading.app.neural.NeuralContext;
import trading.app.neural.NeuralService;

/**
 * Neural network setings panel
 * 
 * @author dima
 * 
 */
public class NetworkPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	/**
	 * Neural context with network settings
	 */
	private final NeuralContext neuralContext;

	/**
	 * Neural service. Will be used to load, save, reset network etc.
	 */
	private final NeuralService neuralService;

	/**
	 * Prediction size control
	 */
	private final JFormattedTextField txtPredictionSize;

	/**
	 * Network layers configuration text field
	 */
	private final JFormattedTextField txtLayers;

	/**
	 * File chooser to load or save network
	 */
	private final JFileChooser fileChooser;

	/**
	 * Create new neural network button
	 */
	private final JButton btnCreate;

	/**
	 * Load neural network from file button
	 */
	private final JButton btnLoad;

	/**
	 * Save neural network to file button
	 */
	private final JButton btnSave;

	/**
	 * Reset neural network weights button
	 */
	private final JButton btnReset;

	/**
	 * Input data window size text control
	 */
	private final JFormattedTextField txtWindowSize;

	/**
	 * How many samples to generate for train
	 */
	private final JFormattedTextField txtTrainSamples;

	/**
	 * How many ideal samples to use during testing
	 */
	private final JFormattedTextField txtPredictionSamples;

	/**
	 * Train step - number of ticks between train samples.
	 */
	private final JFormattedTextField txtTrainStep;

	/**
	 * Label to display necessary data size according to input data size, input
	 * samples, prediction size and prediction samples
	 */
	private final JLabel lblNeededItems;

	/**
	 * Create the panel.
	 */
	public NetworkPanel(NeuralContext context, NeuralService service) {
		neuralContext = context;
		neuralService = service;

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		// General settings panel
		JPanel settingsPanel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, settingsPanel, 60,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, settingsPanel, 24,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, settingsPanel, -10,
				SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, settingsPanel, 343,
				SpringLayout.WEST, this);
		add(settingsPanel);
		SpringLayout sl_settingsPanel = new SpringLayout();
		settingsPanel.setLayout(sl_settingsPanel);

		// Network layers controls
		JLabel label_1 = new JLabel("Layers:");
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, label_1, 10,
				SpringLayout.NORTH, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, label_1, 97,
				SpringLayout.WEST, settingsPanel);
		springLayout.putConstraint(SpringLayout.WEST, label_1, 121,
				SpringLayout.WEST, this);
		settingsPanel.add(label_1);

		txtLayers = new JFormattedTextField("10,10,2");
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, txtLayers, -2,
				SpringLayout.NORTH, label_1);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, txtLayers, 18,
				SpringLayout.EAST, label_1);
		springLayout.putConstraint(SpringLayout.EAST, label_1, -10,
				SpringLayout.WEST, txtLayers);
		springLayout.putConstraint(SpringLayout.EAST, txtLayers, 339,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, txtLayers, 182,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, txtLayers, 100,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, txtLayers, 81,
				SpringLayout.NORTH, this);
		txtLayers.setText("1,2,3");
		txtLayers.setAlignmentY(0.0f);
		txtLayers.setAlignmentX(0.0f);
		settingsPanel.add(txtLayers);

		// Prediction size controls
		JLabel lblPredictionSize = new JLabel("Prediction size:");
		sl_settingsPanel.putConstraint(SpringLayout.WEST, lblPredictionSize,
				40, SpringLayout.WEST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, lblPredictionSize,
				-90, SpringLayout.EAST, settingsPanel);
		springLayout.putConstraint(SpringLayout.WEST, lblPredictionSize, 82,
				SpringLayout.WEST, this);
		settingsPanel.add(lblPredictionSize);

		txtPredictionSize = new JFormattedTextField(new Integer(60));
		sl_settingsPanel.putConstraint(SpringLayout.WEST, txtPredictionSize,
				168, SpringLayout.WEST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, txtPredictionSize,
				-90, SpringLayout.EAST, settingsPanel);
		txtPredictionSize.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateNeededLevel1Count();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, txtPredictionSize, 4,
				SpringLayout.NORTH, lblPredictionSize);
		springLayout.putConstraint(SpringLayout.WEST, txtPredictionSize, 10,
				SpringLayout.EAST, lblPredictionSize);
		txtPredictionSize.setText("60");
		settingsPanel.add(txtPredictionSize);

		// Create button
		btnCreate = new JButton("Create");
		springLayout.putConstraint(SpringLayout.NORTH, btnCreate, 60,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnCreate, -150,
				SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.EAST, btnCreate, -50,
				SpringLayout.EAST, this);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createNetwork();
			}
		});
		add(btnCreate);

		// Load button
		btnLoad = new JButton("Load");
		springLayout.putConstraint(SpringLayout.NORTH, btnLoad, 38,
				SpringLayout.SOUTH, btnCreate);
		springLayout.putConstraint(SpringLayout.WEST, btnLoad, 0,
				SpringLayout.WEST, btnCreate);
		springLayout.putConstraint(SpringLayout.EAST, btnLoad, -50,
				SpringLayout.EAST, this);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadNetwork();
			}
		});
		add(btnLoad);

		// Save button
		btnSave = new JButton("Save");
		springLayout.putConstraint(SpringLayout.NORTH, btnSave, 20,
				SpringLayout.SOUTH, btnLoad);
		springLayout.putConstraint(SpringLayout.WEST, btnSave, 0,
				SpringLayout.WEST, btnCreate);
		springLayout.putConstraint(SpringLayout.EAST, btnSave, 0,
				SpringLayout.EAST, btnCreate);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveNetwork();
			}
		});
		add(btnSave);

		// Reset button
		btnReset = new JButton("Reset");
		springLayout.putConstraint(SpringLayout.NORTH, btnReset, 32,
				SpringLayout.SOUTH, btnSave);
		springLayout.putConstraint(SpringLayout.WEST, btnReset, 0,
				SpringLayout.WEST, btnCreate);
		springLayout.putConstraint(SpringLayout.EAST, btnReset, 0,
				SpringLayout.EAST, btnCreate);
		springLayout.putConstraint(SpringLayout.EAST, txtPredictionSize, -281,
				SpringLayout.WEST, btnReset);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetNetwork();
			}
		});
		add(btnReset);

		// Input data window size
		txtWindowSize = new JFormattedTextField();
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, txtWindowSize, 19,
				SpringLayout.SOUTH, txtLayers);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, txtWindowSize, -90,
				SpringLayout.EAST, settingsPanel);
		txtWindowSize.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateNeededLevel1Count();
				correctNetworkLayersView();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, txtWindowSize, -281,
				SpringLayout.WEST, btnSave);
		springLayout.putConstraint(SpringLayout.NORTH, txtWindowSize, 6,
				SpringLayout.NORTH, btnSave);
		txtWindowSize.setText("100");
		settingsPanel.add(txtWindowSize);

		JLabel lblWindowSize = new JLabel("Window size:");
		sl_settingsPanel.putConstraint(SpringLayout.WEST, txtWindowSize, 16,
				SpringLayout.EAST, lblWindowSize);
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, lblWindowSize, 23,
				SpringLayout.SOUTH, label_1);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, lblWindowSize, 55,
				SpringLayout.WEST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, lblWindowSize, 0,
				SpringLayout.EAST, label_1);
		springLayout.putConstraint(SpringLayout.WEST, txtWindowSize, 10,
				SpringLayout.EAST, lblWindowSize);
		springLayout.putConstraint(SpringLayout.NORTH, lblPredictionSize, 17,
				SpringLayout.SOUTH, lblWindowSize);
		springLayout.putConstraint(SpringLayout.EAST, lblPredictionSize, 0,
				SpringLayout.EAST, lblWindowSize);
		springLayout.putConstraint(SpringLayout.NORTH, lblWindowSize, 7,
				SpringLayout.NORTH, btnSave);
		springLayout.putConstraint(SpringLayout.EAST, lblWindowSize, 0,
				SpringLayout.EAST, label_1);
		settingsPanel.add(lblWindowSize);

		// Samples count
		txtTrainSamples = new JFormattedTextField();
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, txtTrainSamples, 6,
				SpringLayout.SOUTH, txtWindowSize);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, txtTrainSamples, -90,
				SpringLayout.EAST, settingsPanel);
		txtTrainSamples.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateNeededLevel1Count();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, txtTrainSamples, -281,
				SpringLayout.WEST, btnReset);
		springLayout.putConstraint(SpringLayout.SOUTH, txtTrainSamples, 0,
				SpringLayout.SOUTH, btnReset);
		settingsPanel.add(txtTrainSamples);

		JLabel lblSamplesCount = new JLabel("Train samples:");
		sl_settingsPanel.putConstraint(SpringLayout.WEST, txtTrainSamples, 18,
				SpringLayout.EAST, lblSamplesCount);
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, lblSamplesCount, 2,
				SpringLayout.NORTH, txtTrainSamples);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, lblSamplesCount, 46,
				SpringLayout.WEST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, lblSamplesCount, 0,
				SpringLayout.EAST, label_1);
		springLayout.putConstraint(SpringLayout.WEST, txtTrainSamples, 10,
				SpringLayout.EAST, lblSamplesCount);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSamplesCount, 0,
				SpringLayout.SOUTH, btnReset);
		springLayout.putConstraint(SpringLayout.EAST, lblSamplesCount, 0,
				SpringLayout.EAST, label_1);
		settingsPanel.add(lblSamplesCount);

		// Prediction samples
		txtPredictionSamples = new JFormattedTextField();
		sl_settingsPanel.putConstraint(SpringLayout.NORTH,
				txtPredictionSamples, 6, SpringLayout.SOUTH, txtPredictionSize);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, txtPredictionSamples,
				168, SpringLayout.WEST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, txtPredictionSamples,
				-90, SpringLayout.EAST, settingsPanel);
		txtPredictionSamples.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateNeededLevel1Count();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, txtPredictionSamples,
				288, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, txtPredictionSamples,
				182, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, txtPredictionSamples,
				-124, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, txtPredictionSamples,
				-421, SpringLayout.EAST, this);
		settingsPanel.add(txtPredictionSamples);

		JLabel lblPredictionSamples = new JLabel("Prediction samples:");
		sl_settingsPanel.putConstraint(SpringLayout.NORTH,
				lblPredictionSamples, 0, SpringLayout.NORTH,
				txtPredictionSamples);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, lblPredictionSamples,
				10, SpringLayout.WEST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, lblPredictionSamples,
				-90, SpringLayout.EAST, settingsPanel);
		springLayout.putConstraint(SpringLayout.EAST, lblPredictionSamples, 0,
				SpringLayout.EAST, lblWindowSize);
		settingsPanel.add(lblPredictionSamples);

		// Train step
		txtTrainStep = new JFormattedTextField();
		sl_settingsPanel.putConstraint(SpringLayout.EAST, txtTrainStep, -115,
				SpringLayout.EAST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, txtPredictionSize,
				15, SpringLayout.SOUTH, txtTrainStep);
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, lblPredictionSize,
				17, SpringLayout.SOUTH, txtTrainStep);
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, txtTrainStep, 6,
				SpringLayout.SOUTH, txtTrainSamples);
		txtTrainStep.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateNeededLevel1Count();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, txtTrainStep, -16,
				SpringLayout.NORTH, txtPredictionSamples);
		springLayout.putConstraint(SpringLayout.EAST, txtTrainStep, -421,
				SpringLayout.EAST, this);

		settingsPanel.add(txtTrainStep);

		JLabel lblTrainStep = new JLabel("Train step:");
		sl_settingsPanel.putConstraint(SpringLayout.EAST, lblTrainStep, -169,
				SpringLayout.EAST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, txtTrainStep, 18,
				SpringLayout.EAST, lblTrainStep);
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, lblTrainStep, 2,
				SpringLayout.NORTH, txtTrainStep);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, lblTrainStep, 73,
				SpringLayout.WEST, settingsPanel);
		springLayout.putConstraint(SpringLayout.WEST, txtTrainStep, 10,
				SpringLayout.EAST, lblTrainStep);
		springLayout.putConstraint(SpringLayout.NORTH, lblPredictionSamples,
				18, SpringLayout.SOUTH, lblTrainStep);
		springLayout.putConstraint(SpringLayout.NORTH, lblTrainStep, 3,
				SpringLayout.NORTH, txtTrainStep);
		springLayout.putConstraint(SpringLayout.EAST, lblTrainStep, 0,
				SpringLayout.EAST, label_1);
		settingsPanel.add(lblTrainStep);

		JLabel label = new JLabel("Neural Network summary");
		springLayout.putConstraint(SpringLayout.EAST, label, 320,
				SpringLayout.WEST, this);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, label, 10,
				SpringLayout.WEST, this);
		sl_settingsPanel.putConstraint(SpringLayout.SOUTH, label, -5,
				SpringLayout.NORTH, settingsPanel);
		add(label);
		springLayout.putConstraint(SpringLayout.NORTH, label, 23,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, label, 24,
				SpringLayout.WEST, this);
		label.setFont(new Font("Dialog", Font.BOLD, 16));
		springLayout.putConstraint(SpringLayout.NORTH, label_1, 44,
				SpringLayout.SOUTH, label);

		// Required data size info
		lblNeededItems = new JLabel("Items");
		sl_settingsPanel.putConstraint(SpringLayout.EAST, txtLayers, 0,
				SpringLayout.EAST, lblNeededItems);
		sl_settingsPanel.putConstraint(SpringLayout.NORTH, lblNeededItems, 277,
				SpringLayout.NORTH, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.SOUTH, lblNeededItems, -35,
				SpringLayout.SOUTH, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.WEST, lblNeededItems, 10,
				SpringLayout.WEST, settingsPanel);
		sl_settingsPanel.putConstraint(SpringLayout.EAST, lblNeededItems, -10,
				SpringLayout.EAST, settingsPanel);
		lblNeededItems.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNeededItems.setVerticalAlignment(SwingConstants.TOP);
		settingsPanel.add(lblNeededItems);

		// File chooser
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"Network config (.cfg)", "cfg"));

		// Initial view update
		updateView();
	}

	/**
	 * Reset network weights
	 */
	private void resetNetwork() {
		updateContext();
		neuralContext.getNetwork().reset();
		updateView();
	}

	/**
	 * Load network weights from file
	 */
	private void loadNetwork() {
		updateContext();
		// Show open dialog and open
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			neuralService.loadNetwork(fileChooser.getSelectedFile());
		}
		updateView();
	}

	/**
	 * Save network weights to file
	 * 
	 * @return
	 */
	private void saveNetwork() {
		updateContext();

		// Shiw save dialog and save
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String path = file.getAbsolutePath();
			if (!path.endsWith("cfg")) {
				file = new File(path + ".cfg");

			}
			neuralService.saveNetwork(file);
		}
		updateView();
	}

	/**
	 * Create new neural network
	 * 
	 * @throws ParseException
	 */
	private void createNetwork() {

		// int predictionInterval;
		try {
			this.txtPredictionSize.commitEdit();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateContext();

		// Calculate neuron counts in layers
		List<Integer> layers = new ArrayList<Integer>();
		String[] layerStrings = txtLayers.getText().replaceAll("\\s", "")
				.split(",");
		for (String neuronsString : layerStrings) {
			int neurons = Integer.parseInt(neuronsString);
			layers.add(neurons);
		}
		// Create network
		BasicNetwork network = neuralService.createNetwork(layers);
		neuralContext.setNetwork(network);

		// Update view after network creation
		updateView();
	}

	/**
	 * Update view from context
	 */
	public void updateView() {
		if (neuralContext == null) {
			return;
		}

		// Update layers from network
		String layersString = "0,6,2";
		if (neuralContext.getNetwork() != null) {
			// Update layers text
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < neuralContext.getNetwork().getLayerCount(); i++) {
				int neurons = neuralContext.getNetwork().getLayerNeuronCount(i);
				sb.append(neurons);
				sb.append(",");
			}
			layersString = sb.toString().replaceAll(",$", "");
		}
		// Text fields
		txtLayers.setText(layersString);
		txtWindowSize.setText(neuralContext.getLevel1WindowSize().toString());
		txtPredictionSize.setText(neuralContext.getPredictionSize().toString());
		txtTrainSamples.setText(neuralContext.getTrainSamples().toString());
		txtPredictionSamples.setText(neuralContext.getPredictionSamples()
				.toString());
		// Train step spinner
		txtTrainStep.setText(neuralContext.getTrainStep().toString());

		// Buttons
		boolean isNetworkNotNull = (neuralContext.getNetwork() != null);
		btnSave.setEnabled(isNetworkNotNull);
		btnReset.setEnabled(isNetworkNotNull);

		updateNeededLevel1Count();
		correctNetworkLayersView();

	}

	/**
	 * Set network layers first and last layers
	 */
	private void correctNetworkLayersView() {
		// Extract layers from comma delimited string to array
		Integer windowSize = Integer.parseInt(txtWindowSize.getText());
		String layersString = txtLayers.getText();
		String[] layersArray = layersString.split(",");

		// Replace first and last items
		layersArray[0] = new Integer(
				neuralService.getFirstLayerSize(windowSize)).toString();
		layersArray[layersArray.length - 1] = new Integer(
				neuralService.getLastLayerSize()).toString();

		// Convert back to string
		layersString = Arrays.toString(layersArray);

		layersString = layersString.replaceAll("\\[|\\]", "");
		txtLayers.setText(layersString);

	}

	/**
	 * Update level1Neededlabel
	 */
	public void updateNeededLevel1Count() {
		// Get prediction size;
		// int predictionInterval =
		// Integer.parseInt(txtPredictionSize.getText());
		int predictionStep = 1;
		int predictionSamples = Integer
				.parseInt(txtPredictionSamples.getText());
		int predictionSize = predictionStep * predictionSamples;

		// Get trainingSzie
		int windowSize = Integer.parseInt(txtWindowSize.getText());
		int trainSamples = Integer.parseInt(txtTrainSamples.getText());
		int trainStep = Integer.parseInt(txtTrainStep.getText());
		int trainingSize = windowSize + trainStep * (trainSamples - 1);

		String level1NeededString = String
				.format("<html>Required history items: <br/>%d for training, <br/>%d for prediction. <br/><b>%d</b> overall.</html>",
						trainingSize, predictionSize, trainingSize
								+ predictionSize);
		// Set label text
		lblNeededItems.setText(level1NeededString);
	}

	/**
	 * Update context from this view
	 */
	public void updateContext() {
		// Update prediction interval
		int predictionInterval = Integer.parseInt(txtPredictionSize.getText());
		neuralContext.setPredictionSize(predictionInterval);
		// Update window size
		int windowSize = Integer.parseInt(txtWindowSize.getText());
		neuralContext.setLevel1WindowSize(windowSize);
		// Update prediction samples
		int trainSamples = Integer.parseInt(txtTrainSamples.getText());
		neuralContext.setTrainSamples(trainSamples);
		// Update train step
		neuralContext.setTrainStep(Integer.parseInt(txtTrainStep.getText()));

		// Update train samples
		int predictionSamples = Integer
				.parseInt(txtPredictionSamples.getText());
		neuralContext.setPredictionSamples(predictionSamples);

	}
}
