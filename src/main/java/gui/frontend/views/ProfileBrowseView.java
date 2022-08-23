package gui.frontend.views;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.ItemsList;
import gui.frontend.components.base.Text;
import gui.frontend.components.ext.profile.ProfileItemCmp;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ProfileBrowseView extends Div {

    public static class ProfileBrowseViewData {
        public final List<ProfileItemCmp.ProfileItemCmpData> profileDatas;

        public ProfileBrowseViewData(List<ProfileItemCmp.ProfileItemCmpData> profileDatas) {
            this.profileDatas = profileDatas;
        }
    }

    public interface ProfileBrowseViewListener {
        ProfileItemCmp.ProfileItemCmpData onFollow(ProfileItemCmp.ProfileItemCmpData data);

        void onOpen(ProfileItemCmp.ProfileItemCmpData data);
    }

    private final ItemsList<JComponent> cmpItemsList;
    private final Map<String, ProfileItemCmp> profileCmpById;
    private final ProfileItemCmp.ProfileItemCmpListener profileItemCmpListener;
    private ProfileBrowseViewListener listener;
    private ProfileBrowseViewData data;


    public ProfileBrowseView() {
        this.cmpItemsList = new ItemsList<>();
        JScrollPane scrollPane = new JScrollPane(
                cmpItemsList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        //
        this.profileCmpById = Collections.synchronizedMap(new HashMap<>());
        //
        this.profileItemCmpListener = new ProfileItemCmp.ProfileItemCmpListener() {
            @Override
            public void onFollow(ProfileItemCmp.ProfileItemCmpData data) {
                var listr = listener;
                if (listr != null) {
                    ProfileItemCmp.ProfileItemCmpData newData = listr.onFollow(data);
                    ProfileItemCmp profileItemCmp = profileCmpById.getOrDefault(data.handle, null);
                    if (profileItemCmp != null)
                        profileItemCmp.updateData(newData);
                }
            }
        };
        //
        Style titleStyle = Text.defaultStyle();
        titleStyle.setFont(UIConstants.FONT_TITLE_M);
        titleStyle.setForeground(UIConstants.BLUE);
        Text title = new Text("Users", titleStyle);
        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setListener(ProfileBrowseViewListener listener) {
        this.listener = listener;
    }

    public synchronized ProfileBrowseViewData getData() {
        return data;
    }

    public synchronized void updateData(ProfileBrowseViewData data) {
        this.data = data;
        this.profileCmpById.clear();

        List<JComponent> profileItemCmpList = new ArrayList<>(data.profileDatas.size());
        for (ProfileItemCmp.ProfileItemCmpData s : data.profileDatas) {
            ProfileItemCmp profileItemCmp = new ProfileItemCmp();
            profileItemCmp.setListener(this.profileItemCmpListener);
            profileItemCmp.updateData(s);
            profileItemCmpList.add(getUserItemCell(profileItemCmp));
            profileCmpById.put(s.handle, profileItemCmp);
        }
        this.cmpItemsList.setItems(profileItemCmpList);

        repaint();
        revalidate();
    }

    private static Div getUserItemCell(ProfileItemCmp userCmp) {
        Style style = Div.defaultStyle();
        style.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BLACK));
        Div cell = new Div(new GridLayout(1, 1, 0, 0), style);
        cell.add(userCmp);
        return cell;
    }
}
