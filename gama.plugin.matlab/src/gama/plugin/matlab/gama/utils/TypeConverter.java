package gama.plugin.matlab.gama.utils;

import java.util.ArrayList;

import gama.api.runtime.scope.IScope;
import gama.api.types.list.GamaListFactory;
import gama.api.types.list.IList;
import gama.api.types.matrix.GamaMatrixFactory;
import gama.api.gaml.types.Types;

public class TypeConverter {
	public static Object Matlab2GamaType(IScope scope, Object output) {

		if(output instanceof double[]) {
			return GamaListFactory.create(scope, Types.FLOAT, (double[]) output ) ;
		} else if(output instanceof double[][]) {
			// Complicated because we need to transpose the matrix provided by matlab 
			double[][] mat = (double[][]) output;
			if(mat.length == 0) {return 0.0;}
			
			IList<IList<?>> lmat = GamaListFactory.create(Types.LIST);
	
			for(int i = 0;i < mat[0].length ; i++) {
				ArrayList<Double> l = new ArrayList<>();
				for(int j = 0 ; j < mat.length ; j ++ ) {
					l.add(mat[j][i]);
				}
				lmat.add(GamaListFactory.create(scope, Types.FLOAT, l));
			}
			return GamaMatrixFactory.castToMatrix(scope, lmat, null, Types.FLOAT, true);
		}
		 
		// Other cases checked : float and bool
		return output;
	}
}
