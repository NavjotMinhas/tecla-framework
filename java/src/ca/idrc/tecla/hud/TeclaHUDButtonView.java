package ca.idrc.tecla.hud;

import ca.idrc.tecla.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class TeclaHUDButtonView extends ImageButton {
	
	public final static byte POSITION_LEFT = 4;
	public final static byte POSITION_TOPLEFT = 5;
	public final static byte POSITION_TOP = 6;
	public final static byte POSITION_TOPRIGHT = 7;
	public final static byte POSITION_RIGHT = 8;
	public final static byte POSITION_BOTTOMRIGHT = 9;
	public final static byte POSITION_BOTTOM = 10;
	public final static byte POSITION_BOTTOMLEFT = 11;
	
	private Context mContext;
	
    private final Paint mInnerFillPaint = new Paint();
    private final Paint mInnerStrokePaint = new Paint();
    private final Paint mOuterStrokePaint = new Paint();
    private int mWidth;
    private int mHeight;
    private int mStrokeWidth;
	private Path mPath;
	private Bitmap mBackground;
	private Canvas mBackgroundCanvas;
	private byte mPosition;
	private boolean isHighlighted;
	private Drawable mNormalForeground, mHighlightedForeground;

	public TeclaHUDButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
		init();
	}

	private void init() {
		
		mStrokeWidth = 1;
		mInnerFillPaint.setColor(mContext.getResources().getColor(R.color.hud_btn_inner_fill_color));
    	mInnerStrokePaint.setColor(mContext.getResources().getColor(R.color.hud_btn_inner_stroke_color));
    	mOuterStrokePaint.setColor(mContext.getResources().getColor(R.color.hud_btn_outer_stroke_color));
    	mInnerFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    	mInnerStrokePaint.setStyle(Paint.Style.STROKE);
    	mOuterStrokePaint.setStyle(Paint.Style.STROKE);
    	
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = w;
		mHeight = h;

		updateDrawables();
    	
	}
	
	public void setDrawables(Drawable normal, Drawable highlighted) {
		mNormalForeground = normal;
		mHighlightedForeground = highlighted;
	}
	
	public void setProperties(byte position, int stroke_width, boolean highlighted) {
		mPosition = position;
		mStrokeWidth = stroke_width;
		isHighlighted = highlighted;
		updateDrawables();
	}
	
	public void setHighlighted(boolean highlighted) {
		isHighlighted = highlighted;
		updateDrawables();
	}

	public boolean isHighlighted() {
		return isHighlighted;
	}
	
	private void updateDrawables() {
		updateBackground();
		if (isHighlighted) {
			setImageDrawable(mHighlightedForeground);
		} else {
			setImageDrawable(mNormalForeground);
		}
		updatePadding();
		invalidate();
	}
	
	private void updatePadding () {
		int w = getWidth();
		int h = getHeight();
		int outpad = Math.round(h * 0.30f);
		int inpad = Math.round(w * 0.30f);
		switch(mPosition) {
		case POSITION_LEFT:
		case POSITION_RIGHT:
		case POSITION_TOP:
		case POSITION_BOTTOM:
	    	setPadding(0, 0, 0, 0);
			break;
		case POSITION_TOPLEFT:
	    	setPadding(outpad, outpad, inpad, inpad);
			break;
		case POSITION_TOPRIGHT:
	    	setPadding(inpad, outpad, outpad, inpad);
			break;
		case POSITION_BOTTOMLEFT:
	    	setPadding(outpad, inpad, inpad, outpad);
			break;
		case POSITION_BOTTOMRIGHT:
	    	setPadding(inpad, inpad, outpad, outpad);
			break;
		}
	}
	
	private void updateBackground () {
		if (mWidth > 0 && mHeight > 0) {
			
			mBackground = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
	    	mBackgroundCanvas = new Canvas(mBackground);
	    	
	    	int outer_stroke_width = 2 * mStrokeWidth;
	    	mInnerStrokePaint.setStrokeWidth(mStrokeWidth);
	    	mOuterStrokePaint.setStrokeWidth(outer_stroke_width);
	    	mPath = new Path();
	    	int left = outer_stroke_width;
	    	int top = outer_stroke_width;
	    	int right = mWidth - outer_stroke_width;
	    	int bottom = mHeight - outer_stroke_width;
			float side_width = mWidth * Float.parseFloat(mContext.getResources().getString(R.string.side_width_proportion));
			float pad;
			float pie;

			if (mPosition == POSITION_TOPLEFT ||
					mPosition == POSITION_TOPRIGHT ||
					mPosition == POSITION_BOTTOMLEFT ||
					mPosition == POSITION_BOTTOMRIGHT) {				
				pad = 0.2f * right;
				pie = (float) Math.toDegrees(Math.atan(pad / (right - pad)));
				mPath.moveTo(left, bottom - pad);
		    	mPath.arcTo(new RectF(mStrokeWidth, mStrokeWidth, (2 * mWidth), (2 * mHeight)), 180 + pie, 90 - (2 * pie)); //RectF(left, top, right, bottom)
		    	mPath.lineTo(right, side_width);
		    	mPath.lineTo(side_width - outer_stroke_width, bottom);
		    	mPath.lineTo(left, bottom - pad);
		    	float background_rotation = 0.0f;
				switch(mPosition) {
				case POSITION_TOPRIGHT:
					background_rotation = 90.0f;
			    	break;
				case POSITION_BOTTOMLEFT:
					background_rotation = 270.0f;
			    	break;
				case POSITION_BOTTOMRIGHT:
					background_rotation = 180.0f;
			    	break;
				}		    	
		    	Matrix m = new Matrix();
		    	m.setRotate(background_rotation, mWidth/2.0f, mHeight/2.0f);
		    	mPath.transform(m);
			} else {
				switch(mPosition) {
				case POSITION_LEFT:
					pad = 0.2f * right;
					mPath.moveTo(left, top);
			    	mPath.lineTo(right, pad + top);
			    	mPath.lineTo(right, bottom - pad);
			    	mPath.lineTo(left, bottom);
			    	mPath.lineTo(left, top);
			    	break;
				case POSITION_TOP:
					pad = 0.2f * bottom;
					mPath.moveTo(left, top);
			    	mPath.lineTo(right, top);
			    	mPath.lineTo(right - pad, bottom);
			    	mPath.lineTo(left + pad, bottom);
			    	mPath.lineTo(left, top);
			    	break;
				case POSITION_RIGHT:
					pad = 0.2f * right;
					mPath.moveTo(right, top);
			    	mPath.lineTo(right, bottom);
			    	mPath.lineTo(left, bottom - pad);
			    	mPath.lineTo(left, pad + top);
			    	mPath.lineTo(right, top);
			    	break;
				case POSITION_BOTTOM:
					pad = 0.2f * bottom;
					mPath.moveTo(left, bottom);
			    	mPath.lineTo(pad + left, top);
			    	mPath.lineTo(right - pad, top);
			    	mPath.lineTo(right, bottom);
			    	mPath.lineTo(left, bottom);
			    	break;
				}		    	
			}
	    	
	    	mBackgroundCanvas.drawPath(mPath, mOuterStrokePaint);
	    	if (isHighlighted) {
	    		mBackgroundCanvas.drawPath(mPath, mInnerFillPaint);
	    	}
	    	mBackgroundCanvas.drawPath(mPath, mInnerStrokePaint);
	    	setBackground(new BitmapDrawable(getResources(), mBackground));
		}
	}
}
