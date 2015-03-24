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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.content.bricks.KodeyPlayMusicBrick.Tone;
import org.catrobat.catroid.devices.arduino.kodey.Kodey;

public class KodeyPlayToneAction extends TemporalAction {

	private Tone toneEnum;
	private Formula durationInSeconds;
	private Sprite sprite;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {
		int durationInterpretation;

		try {
			durationInterpretation = durationInSeconds.interpretInteger(sprite);
        } catch (InterpretationException interpretationException) {
            durationInterpretation = 0;
            Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
        }

		Kodey kodey = btService.getDevice(BluetoothDevice.KODEY);
		if (kodey == null) {
			return;
		}

		switch (toneEnum) {
			case DO:
				kodey.playTone(0, durationInterpretation);
				break;
			case RE:
				kodey.playTone(1, durationInterpretation);
				break;
			case MI:
				kodey.playTone(2, durationInterpretation);
				break;
			case FA:
				kodey.playTone(3, durationInterpretation);
				break;
			case SO:
				kodey.playTone(4, durationInterpretation);
				break;
			case LA:
				kodey.playTone(5, durationInterpretation);
				break;
			case TI:
				kodey.playTone(6, durationInterpretation);
				break;
		}
	}

	public void setSelectedTone(Tone toneEnum) {
		this.toneEnum = toneEnum;
	}

	public void setDurationInSeconds(Formula durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}