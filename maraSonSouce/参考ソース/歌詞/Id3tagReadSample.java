import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;

// MP3�t�@�C����ID3�^�O��ǂݍ���ŕ\������T���v��
// Java ID3 Tag Library + ���������Ή�
public class Id3tagReadSample extends JFrame {

  public static void main(String[] args) {
    new Id3tagReadSample();
  }

  JTextField fileName = new JTextField(16);
  JTextField title = new JTextField(16);
  JTextField albumTitle = new JTextField(16);
  JTextField track = new JTextField(16);
  JTextField artist = new JTextField(16);

  public Id3tagReadSample() {
    setTitle("mp3�t�@�C����ID3�^�O��\������T���v��(Java ID3 Tag Library + ���������Ή�)");
    setBounds(100, 200, 300, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new FlowLayout());
    // �h���b�v�^�[�Q�b�g�ݒ�
    new DropTarget(this, DnDConstants.ACTION_COPY, new MyDropTargetListener());

    // �ҏW���Ȃ�
    fileName.setEditable(false);
    title.setEditable(false);
    albumTitle.setEditable(false);
    track.setEditable(false);
    artist.setEditable(false);

    // ��ʗv�f�\��t��
    add(new JLabel("mp3�t�@�C�����h���b�v���ĉ�����"));
    putHr();
    putLabel("�t�@�C����:");
    add(fileName);
    putHr();
    putLabel("�A�[�e�B�X�g:");
    add(artist);
    putHr();
    putLabel("�Ȗ�:");
    add(title);
    putHr();
    putLabel("�A���o��:");
    add(albumTitle);
    putHr();
    putLabel("�g���b�N:");
    add(track);
    putHr();

    // ��ʕ\��
    setVisible(true);
  }

  // mp3�t�@�C������ID3�^�O��ǂ�ŕ\�����܂�
  private void readTag(File file) {
    MP3File mp3file;
    try {
      mp3file = new MP3File(file);

      String fileName = file.getName();
      String title = "--";
      String albumTitle = "--";
      String track = "--";
      String artist = "--";
      // v2�^�O��D��I�ɕ\��
      if (mp3file.hasID3v2Tag()) {
        AbstractID3v2 v2 = mp3file.getID3v2Tag();
        title = v2.getSongTitle();
        albumTitle = v2.getAlbumTitle();
        track = v2.getTrackNumberOnAlbum();
        artist = v2.getLeadArtist();

      } else if (mp3file.hasID3v2Tag()) {
        // v2�^�O���Ȃ��ꍇv1�^�O��\��
        ID3v1 v1 = mp3file.getID3v1Tag();

        byte[] ary = v1.getTitle().getBytes("ISO-8859-1");
        title = new String(ary);
        ary = v1.getAlbumTitle().getBytes("ISO-8859-1");
        albumTitle = new String(ary);
        ary = v1.getTrackNumberOnAlbum().getBytes("ISO-8859-1");
        track = new String(ary);
        ary = v1.getArtist().getBytes("ISO-8859-1");
        artist = new String(ary);
      }

      // �ǂݍ��񂾃^�O����ʂɐݒ�
      this.fileName.setText(fileName);
      this.title.setText(title);
      this.artist.setText(artist);
      this.albumTitle.setText(albumTitle);
      this.track.setText(track);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (TagException e) {
      e.printStackTrace();
    }
  }

  // �ȉ���ʐݒ�ADnD�p
  // ���x��
  public void putLabel(String text) {
    JLabel l = new JLabel(text);
    Dimension dim = l.getPreferredSize();
    dim.setSize(85, dim.height);
    l.setPreferredSize(dim);
    l.setHorizontalAlignment(JLabel.RIGHT);
    add(l);
  }

  // ������
  public void putHr() {
    putHr(1000, 0);
  }

  public void putHr(int width, int hight) {
    JSeparator sp = new JSeparator(JSeparator.HORIZONTAL);
    sp.setPreferredSize(new Dimension(width, hight));
    add(sp);
  }

  // �h���b�v�^�[�Q�b�g���X�i�[
  // �h���b�v���ꂽ�t�@�C�����󂯎��A�ŏ��̃t�@�C�������������܂�
  class MyDropTargetListener extends DropTargetAdapter {
    @Override
    public void drop(DropTargetDropEvent dtde) {
      dtde.acceptDrop(DnDConstants.ACTION_COPY);
      boolean b = false;
      try {
        if (dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
          b = true;
          List<File> list = (List<File>) dtde.getTransferable().getTransferData(
              DataFlavor.javaFileListFlavor);
          // �ŏ��̃t�@�C�������擾
          File file = list.get(0);
          readTag(file);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        dtde.dropComplete(b);
      }
    }
  }
}
