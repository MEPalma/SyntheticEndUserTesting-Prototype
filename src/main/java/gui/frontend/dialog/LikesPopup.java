package gui.frontend.dialog;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.ItemsList;
import gui.frontend.components.base.Photo;
import gui.frontend.components.base.Text;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LikesPopup extends JFrame {

    public static class LikesDialogData {
        public final Map<String, String> base64ImgByHandle;

        public LikesDialogData(Map<String, String> base64ImgByHandle) {
            this.base64ImgByHandle = base64ImgByHandle;
        }
    }

    private final ItemsList<JComponent> itemsList;
    private final JScrollPane scrollPane;

    private LikesDialogData data;

    public LikesPopup() {
        super("Likes");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setMinimumSize(new Dimension(300, 400));
        setSize(getMinimumSize());
        setUndecorated(true);

        Style contentPaneStyle = Div.defaultStyle();
        contentPaneStyle.setBorder(BorderFactory.createLineBorder(UIConstants.BLACK, 1, true));
        Div contentPane = new Div(new BorderLayout(0, 4), contentPaneStyle);
        setContentPane(contentPane);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });

        Style titleStyle = Text.defaultStyle();
        titleStyle.setFont(UIConstants.FONT_TITLE_L);
        titleStyle.setForeground(UIConstants.BLUE);
        //
        Text titleText = new Text("Likes", titleStyle);
        add(titleText, BorderLayout.NORTH);

        this.itemsList = new ItemsList<>();

        this.scrollPane = new JScrollPane(
                this.itemsList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        this.scrollPane.getVerticalScrollBar().setUnitIncrement(UIConstants.SCROLL_SPEED_INCREMENT);
        add(this.scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void setVisible(boolean isVisible) {
        setLocationRelativeTo(null);
        super.setVisible(isVisible);
    }

    public synchronized LikesDialogData getData() {
        return this.data;
    }

    public synchronized void updateData(LikesDialogData data) {
        this.data = data;
        List<JComponent> likeCells = new ArrayList<>(data.base64ImgByHandle.size());
        for (var like : data.base64ImgByHandle.entrySet())
            likeCells.add(getLikeCell(like.getKey(), like.getValue()));
        this.itemsList.setItems(likeCells);
        repaint();
        revalidate();
    }

    private Div getLikeCell(String handle, String base64Img) {
        Div likeDiv = new Div(new BorderLayout(3, 0), Div.defaultPaddedStyle());

        Photo profilePhoto = new Photo(Photo.SMALL_SQUARE);
        profilePhoto.updateImage(base64Img);
        likeDiv.add(profilePhoto, BorderLayout.WEST);

        Style handleTextStyle = Text.defaultStyle();
        handleTextStyle.setFont(UIConstants.FONT_TITLE_M);
        Text handleText = new Text(handle, handleTextStyle);
        handleText.setHorizontalAlignment(Text.LEFT);
        likeDiv.add(handleText, BorderLayout.CENTER);

        Style cellStyle = Div.defaultStyle();
        cellStyle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BLACK));
        Div cell = new Div(new GridLayout(), cellStyle);
        cell.add(likeDiv);

        return cell;
    }

}
