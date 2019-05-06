package jp.co.android.kayo.testfx;

import java.util.HashSet;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.pheelicks.visualizer.AudioData;
import com.pheelicks.visualizer.FFTData;
import com.pheelicks.visualizer.renderer.BarGraphRenderer;
import com.pheelicks.visualizer.renderer.Renderer;

public class AudioFxDemo extends Activity{
    private static final String TAG = "AudioFxDemo";

    private static final float VISUALIZER_HEIGHT_DIP = 50f;

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;

    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;
    private TextView mStatusTextView;
    
	AudioRecord audioRec = null;
	boolean bIsRecording = false;
	private AudioTrack audioTrack;
    int bufferSize;
    int SAMPLE_RATE = 44100;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mStatusTextView = new TextView(this);

        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.addView(mStatusTextView);

        setContentView(mLinearLayout);

        // Create the MediaPlayer
        mMediaPlayer = MediaPlayer.create(this, R.raw.test_cbr);
        Log.d(TAG, "MediaPlayer audio session ID: " + mMediaPlayer.getAudioSessionId());

        setupVisualizerFxAndUI();
        setupEqualizerFxAndUI();
//        mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
//        mVisualizerView.link(mMediaPlayer);
		// AudioRecordの作成
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
		        AudioFormat.CHANNEL_CONFIGURATION_MONO,
		        AudioFormat.ENCODING_PCM_16BIT);
		audioRec = new AudioRecord(MediaRecorder.AudioSource.MIC,
		        SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
		        AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		
		audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

        
        // Make sure the visualizer is enabled only when you actually want to receive data, and
        // when it makes sense to receive data.
//        mVisualizer.setEnabled(true);

        // When the stream ends, we don't need to collect any more data. We don't do this in
        // setupVisualizerFxAndUI because we likely want to have more, non-Visualizer related code
        // in this callback.
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                mVisualizer.setEnabled(false);
//            }
//        });
        mVisualizerView.link2(audioTrack);
		audioTrack.play();
//        mMediaPlayer.start();
        recodingAndPlay();
        mStatusTextView.setText("Playing audio...");
        addBarGraphRenderers();
        
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if(keyCode==KeyEvent.KEYCODE_BACK){
    	  bIsRecording = false;
    	  finish();
        return false;
      }
      return false;
    }
    private void recodingAndPlay() {
		if (bIsRecording) {
			bIsRecording = false;
		} else {
			// 録音開始
			Log.v("AudioRecord", "startRecording");
			audioRec.startRecording();
			bIsRecording = true;
			// 録音スレッド
			new Thread(new Runnable() {
				@Override
				public void run() {
					byte buf[] = new byte[bufferSize];
					// TODO Auto-generated method stub
					while (bIsRecording) {
						// 録音データ読み込み
						audioRec.read(buf, 0, buf.length);
						audioTrack.write(buf, 0, buf.length);
						// Log.v("AudioRecord", "read " + buf.length +
						// " bytes");
					}
					// 録音停止
					Log.v("AudioRecord", "stop");
					audioRec.stop();
				}
			}).start();
		}
	}

    
    // Methods for adding renderers to visualizer
    private void addBarGraphRenderers()
    {
      Paint paint = new Paint();
      //バーの幅
//      paint.setStrokeWidth(50f);
      paint.setStrokeWidth(12f);
      paint.setAntiAlias(true);
      paint.setColor(Color.argb(200, 56, 138, 252));
      //バーの間隔
//      BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
      BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(4, paint, false);
      mVisualizerView.addRenderer(barGraphRendererBottom);


    }
    private void setupEqualizerFxAndUI() {
        // Create the Equalizer object (an AudioEffect subclass) and attach it to our media player,
        // with a default priority (0).
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        TextView eqTextView = new TextView(this);
        eqTextView.setText("Equalizer:");
        mLinearLayout.addView(eqTextView);

        short bands = mEqualizer.getNumberOfBands();

        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            final short band = i;

            TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            freqTextView.setText((mEqualizer.getCenterFreq(band) / 1000) + " Hz");
            mLinearLayout.addView(freqTextView);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setText((minEQLevel / 100) + " dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEQLevel / 100) + " dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            SeekBar bar = new SeekBar(this);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(mEqualizer.getBandLevel(band));

            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                        boolean fromUser) {
                    mEqualizer.setBandLevel(band, (short) (progress + minEQLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            row.addView(minDbTextView);
            row.addView(bar);
            row.addView(maxDbTextView);

            mLinearLayout.addView(row);
        }
    }

    private void setupVisualizerFxAndUI() {
        // Create a VisualizerView (defined below), which will render the simplified audio
        // wave form to a Canvas.
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                (int)(VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                    int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mEqualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}

/**
 * A simple class that draws waveform data received from a
 * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
 */
@SuppressLint("DrawAllocation")
class VisualizerView extends View{
	  @SuppressWarnings("unused")
	  private static final String TAG = "VisualizerView";

	    private byte[] mBytes;
	    private byte[] mFFTBytes;
	    private Rect mRect = new Rect();
	    private Visualizer mVisualizer;

	    private Set<Renderer> mRenderers;

	    private Paint mFlashPaint = new Paint();
	    private Paint mFadePaint = new Paint();

	    public VisualizerView(Context context, AttributeSet attrs, int defStyle)
	    {
	      super(context, attrs);
	      init();
	    }

	    public VisualizerView(Context context, AttributeSet attrs)
	    {
	      this(context, attrs, 0);
	    }

	    public VisualizerView(Context context)
	    {
	      this(context, null, 0);
	    }

	    private void init() {
	      mBytes = null;
	      mFFTBytes = null;

	      mFlashPaint.setColor(Color.argb(110, 255, 255, 255));
//	      mFadePaint.setColor(Color.argb(238, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
	      //うごめきの早さ
	      mFadePaint.setColor(Color.argb(220, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
//	      mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
	      mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));

	      mRenderers = new HashSet<Renderer>();
	    }

	    /**
	     * Links the visualizer to a player
	     * @param player - MediaPlayer instance to link to
	     */
	    public void link(MediaPlayer player)
	    {
	      if(player == null)
	      {
	        throw new NullPointerException("Cannot link to null MediaPlayer");
	      }

	      // Create the Visualizer object and attach it to our media player.
	      mVisualizer = new Visualizer(player.getAudioSessionId());
	      mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

	      // Pass through Visualizer data to VisualizerView
	      Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
	      {
	        @Override
	        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
	            int samplingRate)
	        {
	          updateVisualizer(bytes);
	        }

	        @Override
	        public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
	            int samplingRate)
	        {
	          updateVisualizerFFT(bytes);
	        }
	      };
	      
	      mVisualizer.setDataCaptureListener(captureListener,
	          Visualizer.getMaxCaptureRate() / 2, true, true);

	      // Enabled Visualizer and disable when we're done with the stream
	      mVisualizer.setEnabled(true);
	      player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
	      {
	        @Override
	        public void onCompletion(MediaPlayer mediaPlayer)
	        {
	          mVisualizer.setEnabled(false);
	        }
	      });
	    }

	    public void link2(final AudioTrack record)
	    {
	      if(record == null)
	      {
	        throw new NullPointerException("Cannot link to null MediaPlayer");
	      }

	      // Create the Visualizer object and attach it to our media player.
	      
	      mVisualizer = new Visualizer(record.getAudioSessionId());
	      mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
	      // Pass through Visualizer data to VisualizerView
	      Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
	      {
	        @Override
	        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
	            int samplingRate)
	        {
	          updateVisualizer(bytes);
	        }

	        @Override
	        public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
	            int samplingRate)
	        {
	          updateVisualizerFFT(bytes);
	        }
	      };
	      
	      mVisualizer.setDataCaptureListener(captureListener,
	          Visualizer.getMaxCaptureRate() / 2, true, true);

	      // Enabled Visualizer and disable when we're done with the stream
	      mVisualizer.setEnabled(true);

	  }
	 
	    
	    public void addRenderer(Renderer renderer)
	    {
	      if(renderer != null)
	      {
	        mRenderers.add(renderer);
	      }
	    }

	    public void clearRenderers()
	    {
	      mRenderers.clear();
	    }

	    /**
	     * Call to release the resources used by VisualizerView. Like with the
	     * MediaPlayer it is good practice to call this method
	     */
	    public void release()
	    {
	      mVisualizer.release();
	    }

	    /**
	     * Pass data to the visualizer. Typically this will be obtained from the
	     * Android Visualizer.OnDataCaptureListener call back. See
	     * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
	     * @param bytes
	     */
	    public void updateVisualizer(byte[] bytes) {
	      mBytes = bytes;
	      invalidate();
	    }

	    /**
	     * Pass FFT data to the visualizer. Typically this will be obtained from the
	     * Android Visualizer.OnDataCaptureListener call back. See
	     * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
	     * @param bytes
	     */
	    public void updateVisualizerFFT(byte[] bytes) {
	      mFFTBytes = bytes;
	      invalidate();
	    }

	    boolean mFlash = false;

	    /**
	     * Call this to make the visualizer flash. Useful for flashing at the start
	     * of a song/loop etc...
	     */
	    public void flash() {
	      mFlash = true;
	      invalidate();
	    }

	    Bitmap mCanvasBitmap;
	    Canvas mCanvas;



	@SuppressLint("DrawAllocation")
	@Override
	    protected void onDraw(Canvas canvas) {
	      super.onDraw(canvas);

	      // Create canvas once we're ready to draw
	      mRect.set(0, 0, getWidth(), getHeight());

	      if(mCanvasBitmap == null)
	      {
	        mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
	      }
	      if(mCanvas == null)
	      {
	        mCanvas = new Canvas(mCanvasBitmap);
	      }

	      if (mBytes != null) {
	        // Render all audio renderers
	        AudioData audioData = new AudioData(mBytes);
	        for(Renderer r : mRenderers)
	        {
	          r.render(mCanvas, audioData, mRect);
	        }
	      }

	      if (mFFTBytes != null) {
	        // Render all FFT renderers
	        FFTData fftData = new FFTData(mFFTBytes);
	        for(Renderer r : mRenderers)
	        {
	          r.render(mCanvas, fftData, mRect);
	        }
	      }

	      // Fade out old contents
	      mCanvas.drawPaint(mFadePaint);

	      if(mFlash)
	      {
	        mFlash = false;
	        mCanvas.drawPaint(mFlashPaint);
	      }

	      canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
	    }



}