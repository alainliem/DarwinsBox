/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a clone of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.util.math.container;

import darwin.util.math.base.Quaternion;
import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;
import darwin.util.math.composits.ModelMatrix;

/**
 *
 * @author dheinrich
 */
public class SimpleTransformation implements TransformationContainer
{
    protected ModelMatrix matrix;

    public SimpleTransformation() {
        matrix = new ModelMatrix();
    }

    protected SimpleTransformation(ModelMatrix m) {
        matrix = m;
    }

    @Override
    public Vector3 getPosition() {
        return matrix.getTranslation();
    }

    @Override
    public void setPosition(ImmutableVector<Vector3> newpos) {
        newpos.clone().sub(getPosition());
        matrix.worldTranslate(newpos);
    }

    @Override
    public void shiftWorldPosition(ImmutableVector<Vector3> delta) {
        matrix.worldTranslate(delta);
    }

    @Override
    public void shiftRelativePosition(ImmutableVector<Vector3> delta) {
        matrix.translate(delta);
    }

    @Override
    public void rotateEuler(ImmutableVector<Vector3> delta) {
        matrix.rotateEuler(delta);
    }

    @Override
    public void rotate(Matrix4 rotmat) {
        matrix.rotateEuler(rotmat.getEularAngles());
    }

    @Override
    public void scale(ImmutableVector<Vector3> delta) {
        matrix.scale(delta);
    }

    @Override
    public void scale(float delta) {
        matrix.scale(delta);
    }

    //TODO muss es hier ein clone geben?
    @Override
    public ModelMatrix getModelMatrix() {
        return matrix;
    }

    @Override
    public void setWorldPosition(ImmutableVector<Vector3> pos) {
        matrix.setWorldTranslate(pos);
    }

    protected ModelMatrix getMatrix() {
        return matrix;
    }

    @Override
    public void rotate(Quaternion rotation) {
        matrix.rotate(rotation);
    }

    @Override
    public Quaternion getRotation() {
        return matrix.getRotation();
    }

    @Override
    public void setRotation(Quaternion rot) {
        setRotation(rot.getRotationMatrix());
    }

    @Override
    public void setRotation(Matrix4 rot) {
        matrix.setRotation(rot);
    }

    @Override
    public void reset() {
        matrix.loadIdentity();
    }
}
