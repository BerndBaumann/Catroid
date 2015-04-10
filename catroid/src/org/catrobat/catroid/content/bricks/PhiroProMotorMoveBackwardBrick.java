/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.PhiroProMotorBackwardSingleSeekbarFragment;

import java.util.List;

public class PhiroProMotorMoveBackwardBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	private String motor;
	private transient Motor motorEnum;
	private transient TextView editSpeed;
	private Formula speed;
	private Boolean isFormulaEditorPreview = false;

	public void setIsFormulaEditorPreview(Boolean isFormulaEditorPreview) {
		this.isFormulaEditorPreview = isFormulaEditorPreview;
	}

	public static enum Motor {
		MOTOR_LEFT, MOTOR_RIGHT, MOTOR_BOTH
	}

	public PhiroProMotorMoveBackwardBrick() {
		addAllowedBrickField(BrickField.PHIRO_PRO_SPEED);
	}

	public PhiroProMotorMoveBackwardBrick(Motor motor, int speedValue) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		this.speed = new Formula(speedValue);
	}

	public PhiroProMotorMoveBackwardBrick(Motor motor, Formula speedFormula) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		this.speed = speedFormula;
	}

	public void setSpeedTextValue(int speed)
	{
//		editSpeed.setText(String.valueOf(speed));
//		this.speed.setDisplayText(String.valueOf(speed));
		this.speed.setRoot(new Formula(speed).getRoot());

	}

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_PHIRO_PRO;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_phiro_pro_motor_backward, null);
		TextView textSpeed = (TextView) prototypeView.findViewById(R.id.brick_phiro_pro_motor_backward_action_speed_text_view);
		textSpeed.setText(String.valueOf(BrickValues.PHIRO_PRO_SPEED));

		Spinner phiroProMotorSpinner = (Spinner) prototypeView.findViewById(R.id.brick_phiro_pro_motor_backward_action_spinner);
		phiroProMotorSpinner.setFocusableInTouchMode(false);
		phiroProMotorSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.brick_phiro_pro_select_motor_spinner,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		phiroProMotorSpinner.setAdapter(motorAdapter);
		phiroProMotorSpinner.setSelection(motorEnum.ordinal());
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new PhiroProMotorMoveBackwardBrick(motorEnum, speed.clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_phiro_pro_motor_backward, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_phiro_pro_motor_backward_action_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textSpeed = (TextView) view.findViewById(R.id.brick_phiro_pro_motor_backward_action_speed_text_view);
		editSpeed = (TextView) view.findViewById(R.id.brick_phiro_pro_motor_backward_action_speed_edit_text);
		speed.setTextFieldId(R.id.brick_phiro_pro_motor_backward_action_speed_edit_text);
		speed.refreshTextField(view);

		textSpeed.setVisibility(View.GONE);
		editSpeed.setVisibility(View.VISIBLE);

		editSpeed.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.brick_phiro_pro_select_motor_spinner,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_phiro_pro_motor_backward_action_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			motorSpinner.setClickable(true);
			motorSpinner.setEnabled(true);
		} else {
			motorSpinner.setClickable(false);
			motorSpinner.setEnabled(false);
		}

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				motorEnum = Motor.values()[position];
				motor = motorEnum.name();
				adapterView = arg0;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		motorSpinner.setSelection(motorEnum.ordinal());

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		if ((speed.getRoot().getElementType() == FormulaElement.ElementType.NUMBER) && (isFormulaEditorPreview == false)) {
			PhiroProMotorBackwardSingleSeekbarFragment.showSingleSeekBarFragment(view, this, speed);
		} else {
			FormulaEditorFragment.showFragment(view, this, speed);
		}
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_phiro_pro_motor_backward_action_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textPhiroProMotorActionLabel = (TextView) view.findViewById(R.id.brick_phiro_pro_motor_backward_action_label);
			TextView textPhiroProMotorActionSpeed = (TextView) view.findViewById(R.id.brick_phiro_pro_motor_backward_action_speed);
			TextView textPhiroProMotorActionPercent = (TextView) view.findViewById(R.id.brick_phiro_pro_motor_backward_action_percent);
			TextView textPhiroProMotorActionLabelSpeedView = (TextView) view
					.findViewById(R.id.brick_phiro_pro_motor_backward_action_speed_text_view);
			TextView editSpeed = (TextView) view.findViewById(R.id.brick_phiro_pro_motor_backward_action_speed_edit_text);

			textPhiroProMotorActionLabel.setTextColor(textPhiroProMotorActionLabel.getTextColors().withAlpha(alphaValue));
			textPhiroProMotorActionSpeed.setTextColor(textPhiroProMotorActionSpeed.getTextColors().withAlpha(alphaValue));
			textPhiroProMotorActionPercent.setTextColor(textPhiroProMotorActionPercent.getTextColors().withAlpha(alphaValue));
			textPhiroProMotorActionLabelSpeedView.setTextColor(textPhiroProMotorActionLabelSpeedView.getTextColors().withAlpha(
					alphaValue));
			Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_phiro_pro_motor_backward_action_spinner);
			ColorStateList color = textPhiroProMotorActionLabelSpeedView.getTextColors().withAlpha(alphaValue);
			motorSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			editSpeed.setTextColor(editSpeed.getTextColors().withAlpha(alphaValue));
			editSpeed.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.phiroProMotorMoveBackwardAction(sprite, motorEnum,
				speed));
		return null;
	}

}