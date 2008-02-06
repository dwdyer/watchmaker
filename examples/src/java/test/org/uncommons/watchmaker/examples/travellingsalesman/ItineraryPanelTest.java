package org.uncommons.watchmaker.examples.travellingsalesman;

import java.awt.BorderLayout;
import java.util.Collection;
import javax.swing.JFrame;
import org.fest.swing.core.RobotFixture;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Basic unit test for the {@link ItineraryPanel} class.  Makes sure that the
 * buttons operate as expected so that the right cities are returned.
 * @author Daniel Dyer
 */
public class ItineraryPanelTest
{
    private static final TestDistances CITIES = new TestDistances();

    private RobotFixture robot;

    @BeforeMethod
    public void prepare()
    {
        robot = RobotFixture.robotWithNewAwtHierarchy();
    }


    @AfterMethod
    public void cleanUp()
    {
        robot.cleanUp();
        robot = null;
    }
    

    @Test(groups = "display-required") // Will fail if run in a headless environment (such as hudson.uncommons.org).
    public void testSelectAll()
    {
        ItineraryPanel itineraryPanel = new ItineraryPanel(CITIES.getKnownCities());
        JFrame frame = new JFrame();
        frame.add(itineraryPanel, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(100, 300);
        frame.validate();
        frame.setVisible(true);
        assert itineraryPanel.getSelectedCities().isEmpty() : "Should be no cities selected initially.";
        frameFixture.button("All").click();
        Collection<String> selectedCities = itineraryPanel.getSelectedCities();
        assert selectedCities.size() == CITIES.getKnownCities().size()
            : "All cities should be selected after button click.";
    }


    @Test(groups = "display-required", // Will fail if run in a headless environment (such as hudson.uncommons.org).
          dependsOnMethods = "testSelectAll")
    public void testSelectNone()
    {
        ItineraryPanel itineraryPanel = new ItineraryPanel(CITIES.getKnownCities());
        JFrame frame = new JFrame();
        frame.add(itineraryPanel, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(100, 300);
        frame.validate();
        frame.setVisible(true);
        frameFixture.button("All").click();
        Collection<String> selectedCities = itineraryPanel.getSelectedCities();
        assert selectedCities.size() == CITIES.getKnownCities().size()
            : "All cities should be selected after all button click.";
        frameFixture.button("None").click();
        assert itineraryPanel.getSelectedCities().isEmpty()
            : "No cities should be selected after clear button is clicked.";
    }
}
