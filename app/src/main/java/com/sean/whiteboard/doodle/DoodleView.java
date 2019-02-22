package com.sean.whiteboard.doodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Xfermode;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.sean.whiteboard.BoardCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchao on 2019/2/13.
 */

public class DoodleView extends View {
	private static final int CACHE_TRACE_COUNT = 5; // 缓存笔画数
	private Paint paint; // 画笔
	private Path path; // 绘制path
	private Xfermode eraseXfermode; // 合成方式 用于橡皮擦除
	private Bitmap bufferBitmap; // 缓存绘制内容
	private Canvas bufferCanvas; // 缓存绘制画布
	private Bitmap holdBitmap; // 固化绘制内容
	private Canvas holdCanvas; // 固化内容画布
	private Shape currentShape; // 当前图形

	private float startX; // 触点x坐标
	private float startY; // 触点y坐标

	private SparseArray<Shape> shapeArray; // 图形缓存

	private List<Shape> paintedTraceList; // 缓存已绘制笔画
	private List<Shape> revokedTraceList; // 缓存已撤销笔画

	@Mode
	private int mode = BoardCommand.DRAW; // 绘制模式 画图/拖动

	private boolean isActionDone; // 一次绘制是否完成

	private ScaleGestureDetector scaleGestureDetector; //
	private GestureDetector gestureDetector; //
	// 画布当前的 Matrix， 用于获取当前画布的一些状态信息，例如缩放大小，平移距离等
	private Matrix mCanvasMatrix = new Matrix();
	// 将用户触摸的坐标转换为画布上坐标所需的 Matrix， 以便找到正确的缩放中心位置
	private Matrix mInvertMatrix = new Matrix();

	// 所有用户触发的缩放、平移等操作都通过下面的 Matrix 直接作用于画布上，
	// 将系统计算的一些初始缩放平移信息与用户操作的信息进行隔离，让操作更加直观
	private Matrix mUserMatrix = new Matrix();
	private float[] matrixValues = new float[9];
	private static final float MAX_SCALE = 4.0f;    //最大缩放比例
	private static final float MIN_SCALE = 0.5f;    // 最小缩放比例

	public DoodleView(Context context) {
		this(context, null);
	}

	public DoodleView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		setColor(Color.CYAN); // 设置画笔默认颜色
		setStockWidth(5); // 设置画笔默认宽度
		path = new Path();
		shapeArray = new SparseArray<>();
		setShapeType(ShapeType.LINE); // 设置默认图形
		paintedTraceList = new ArrayList<>(CACHE_TRACE_COUNT);
		revokedTraceList = new ArrayList<>(CACHE_TRACE_COUNT);
		initGesture(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != 0 && h != 0) {
			bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			bufferCanvas = new Canvas(bufferBitmap);

			holdBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			holdCanvas = new Canvas(holdBitmap);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mode == BoardCommand.DRAG){
			Log.e(" ======DoodleView"," x:" + event.getX() +" y:"+event.getY());
			boolean handled = gestureDetector.onTouchEvent(event);
			if (!handled) {
				scaleGestureDetector.onTouchEvent(event);
			}
			return false;
		}

		if (currentShape == null) {
			return false;
		}
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				isActionDone = false;
				currentShape.reset();
				clearBufferCanvas();
				startX = event.getX();
				startY = event.getY();
				path.moveTo(startX, startY);

				try {
					// 保存画笔
					paintedTraceList.add((Shape) currentShape.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				paintedTraceList.get(paintedTraceList.size() - 1).onMove(startX,
						startY, event.getX(), event.getY());
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				isActionDone = true;
				invalidate();
				break;
		}
		return mode == BoardCommand.DRAW;
	}

	// 手势处理
	private void initGesture(Context context) {
		gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				float scale = getMatrixValue(Matrix.MSCALE_X, mCanvasMatrix);
				mUserMatrix.preTranslate(-distanceX / scale, -distanceY / scale);
				invalidate();
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				return false;
			}
		});

		scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				float scaleFactor = detector.getScaleFactor();
				float fx = detector.getFocusX();
				float fy = detector.getFocusY();
				float[] points = mapPoint(fx, fy, mInvertMatrix);
				scaleFactor = getRealScaleFactor(scaleFactor);
				mUserMatrix.preScale(scaleFactor, scaleFactor, points[0], points[1]);
				//fixTranslate();
				invalidate();
				return true;
			}

		});
	}

	private float getMatrixValue(int name, Matrix matrix) {
		matrix.getValues(matrixValues);
		return matrixValues[name];
	}

	//--- 将坐标转换为画布坐标 ---
	private float[] mapPoint(float x, float y, Matrix matrix) {
		float[] temp = new float[2];
		temp[0] = x;
		temp[1] = y;
		matrix.mapPoints(temp);
		return temp;
	}

	private float getRealScaleFactor(float currentScaleFactor) {
		float realScale;
		float userScale = getMatrixValue(Matrix.MSCALE_X, mUserMatrix);    // 用户当前的缩放比例
		float theoryScale = userScale * currentScaleFactor;           // 理论缩放数值

		// 如果用户在执行放大操作并且理论缩放数据大于4.0
		if (currentScaleFactor > 1.0f && theoryScale > MAX_SCALE) {
			realScale = MAX_SCALE / userScale;
		} else if (currentScaleFactor < 1.0f && theoryScale < MIN_SCALE) {
			realScale = MIN_SCALE / userScale;
		} else {
			realScale = currentScaleFactor;
		}
		return realScale;
	}

	/**
	 * 设置图形
	 *
	 * @param shapeType
	 *            图形类型
	 */
	public void setShapeType(@ShapeType int shapeType) {
		currentShape = getCurrentShape(shapeType);
	}

	/**
	 * 获取当前绘制图形对象
	 *
	 * @param shapeType
	 *            图形类型
	 * @return
	 */
	private Shape getCurrentShape(@ShapeType int shapeType) {
		if (shapeArray.indexOfKey(shapeType) >= 0) {
			return shapeArray.get(shapeType);
		}
		Shape shape;
		switch (shapeType) {
			case ShapeType.PATH:
			default:
				shape = new com.sean.whiteboard.doodle.Path(paint, path);
				break;
			case ShapeType.LINE:
				shape = new Line(paint, path);
				break;
			case ShapeType.RECT:
				shape = new Rectangle(paint, path);
				break;
			case ShapeType.OVAL:
				shape = new Oval(paint, path);
				break;
		}
		shapeArray.append(shapeType, shape);
		return shape;
	}

	/**
	 * 设置画笔宽度
	 *
	 * @param stockWidth
	 */
	public void setStockWidth(int stockWidth) {
		paint.setStrokeWidth(stockWidth);
	}

	/**
	 * 设置画笔颜色
	 *
	 * @param color
	 */
	public void setColor(int color) {
		paint.setColor(color);
	}

	/**
	 * 设置绘制模式
	 *
	 * @param mode
	 */
	public void setMode(@Mode int mode) {
		this.mode = mode;
//		switch (mode) {
//			case BoardCommand.DRAW:
//				paint.setXfermode(null);
//				break;
//			case BoardCommand.ERASE:
//				if (eraseXfermode == null) {
//					eraseXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
//				}
//				paint.setXfermode(eraseXfermode);
//				break;
//		}
	}

	public void revoke(){
		reDraw(paintedTraceList);
	}

	public void recovery(){
		reDraw(revokedTraceList);
	}

	private void reDraw(List<Shape> traceList) {
		if (!traceList.isEmpty()) {
			Shape shape = traceList.remove(traceList.size() - 1);
			if (traceList == paintedTraceList) {
				revokedTraceList.add(shape);
			} else {
				paintedTraceList.add(shape);
			}
			clearBufferCanvas();
			invalidate();
		}
	}

	private void clearBufferCanvas() {
		bufferCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		while (paintedTraceList.size() > CACHE_TRACE_COUNT) {
			Shape shape = paintedTraceList.remove(0);
			shape.onDraw(holdCanvas);
		}

		bufferCanvas.drawBitmap(holdBitmap, 0f, 0f, null);

		for (int i = 0; i < paintedTraceList.size(); i++) {
			Shape shape = paintedTraceList.get(i);
			if (i < paintedTraceList.size() - 1 || shape.isSerial()
					|| isActionDone) {
				shape.onDraw(bufferCanvas);
			}
		}

		canvas.save();
		canvas.concat(mUserMatrix);

		mCanvasMatrix = canvas.getMatrix();
		mCanvasMatrix.invert(mInvertMatrix);

		canvas.drawBitmap(bufferBitmap, 0f, 0f, null);

		if (!isActionDone && paintedTraceList.size() > 0){
			Shape current = paintedTraceList.get(paintedTraceList.size() - 1);
			if (!current.isSerial()) {
				current.onDraw(canvas);
			}
		}

		canvas.restore();

	}

	@IntDef({ BoardCommand.DRAW, BoardCommand.DRAG })
	public @interface Mode {

	}
}
