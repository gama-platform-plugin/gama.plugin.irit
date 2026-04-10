/*******************************************************************************************************
 *
 * GamaGeoJsonFile.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.plugin.cim.files;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.citygml4j.cityjson.CityJSONContext;
import org.citygml4j.cityjson.CityJSONContextException;
import org.citygml4j.cityjson.reader.CityJSONInputFactory;
import org.citygml4j.cityjson.reader.CityJSONReadException;
import org.citygml4j.cityjson.reader.CityJSONReader;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.core.model.core.AbstractCityObject;
import org.citygml4j.core.model.core.AbstractCityObjectProperty;
import org.citygml4j.core.model.core.AbstractSpace;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.CityModel;
import org.citygml4j.core.model.generics.GenericOccupiedSpace;
import org.citygml4j.core.visitor.ObjectWalker;
import org.xmlobjects.gml.model.geometry.AbstractGeometry;
import org.xmlobjects.gml.model.geometry.DirectPositionList;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.GeometryProperty;
import org.xmlobjects.gml.model.geometry.primitives.LinearRing;

import gama.annotations.doc;
import gama.annotations.file;
import gama.annotations.support.IConcept;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.GamaGeometryType;
import gama.api.types.geometry.GamaPointFactory;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.Types;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.GamaPoint;
import gama.api.types.geometry.IPoint;
import gama.api.utils.geometry.GamaEnvelopeFactory;
import gama.api.types.geometry.IShape;
import gama.api.types.list.GamaListFactory;
import gama.api.types.list.IList;
// Envelope3D usage removed; use IEnvelope from file base class instead
import gama.api.utils.geometry.IEnvelope;
import gama.core.util.file.GamaGeometryFile;
import gama.gaml.operators.spatial.SpatialCreation;


/**
 * The Class GamaGeoJsonFile.
 */
@file (
		name = "cityjson",
		extensions = { "json" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.GIS, IConcept.FILE },
		doc = @doc ("Represents geospatial files written using the GeoJSON format. The internal representation is a list of geometries"))
public class GamaCityJsonFile extends GamaGeometryFile {
	
	// Envelope is obtained from the geometry when needed
	private IEnvelope envelope;

	public GamaCityJsonFile(IScope scope, String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		
	}
	
	@Override
	protected IShape buildGeometry(final IScope scope) {
		// Build a geometry collection from the buffered shapes
		return SpatialCreation.geometryCollection(scope, getBuffer());
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readShapes(scope);
	}

	@Override
	public IEnvelope computeEnvelope(final IScope scope) {
		// Delegate to the geometry's envelope (GamaGeometryFile expects IEnvelope)
		try {
			final IShape g = getGeometry(scope);
			return g == null ? null : g.getEnvelope();
		} catch (final Exception e) {
			return null;
		}
	}
	
	public IShape buildShape(IScope scope,AbstractGeometry geometry ) {
		if (geometry instanceof LinearRing) {
			LinearRing lr = (LinearRing) geometry;
			DirectPositionList pts = lr.getControlPoints().getPosList();
			IList<IPoint> points = GamaListFactory.create(Types.POINT);
			List<Double> v = pts.getValue();
			for(int i = 0; i < v.size() - 2; i= i+3) {
				points.add( GamaPointFactory.create(v.get(i), v.get(i+1), v.get(i+2)) );
			}
			return SpatialCreation.polygon(scope, points);
		}
		return null;
	}
	
	public IShape BuildGeometriesGeom(IScope scope, List<GeometryProperty<?>> geoms) {
		if (geoms == null || geoms.isEmpty()) return null;
		IList<IShape> faces = GamaListFactory.create();
		geoms.forEach(g -> g.getObject().accept(
				new ObjectWalker() {
					@Override
					public void visit(AbstractGeometry geometry) {
						System.out.println("- child "+geometry.getClass().getSimpleName()); 
						IShape fS = buildShape(scope, geometry);
						if (fS != null)
							faces.add(fS);
						super.visit(geometry);
					}
				}));
		
		return SpatialCreation.geometryCollection(scope, faces);
	}

	
	public IShape BuildGeometriesSpace(IScope scope, List<AbstractSpaceBoundaryProperty> geoms) {
		if (geoms == null || geoms.isEmpty()) return null;
		IList<IShape> faces = GamaListFactory.create();
		geoms.forEach(g -> g.getObject().accept(
				new ObjectWalker() {
					@Override
					public void visit(AbstractGeometry geometry) {
						System.out.println("- child "+geometry.getClass().getSimpleName()); 
						IShape fS = buildShape(scope, geometry);
						if (fS != null)
							faces.add(fS);
						super.visit(geometry);
					}
				}));
		
		return SpatialCreation.geometryCollection(scope, faces);
	}

	
	protected void readShapes(final IScope scope) {
		try {
			
			
	        CityJSONContext context = CityJSONContext.newInstance();
			
	        CityJSONInputFactory in = context.createCityJSONInputFactory();

	        CityModel cityModel = null;
	        try (CityJSONReader reader = in.createCityJSONReader(getFile(scope).toPath())) {
	            cityModel = (CityModel) reader.next();
	        } catch (CityJSONReadException e) {
				e.printStackTrace();
			}
	        
	        cityModel.getFeatureMembers().forEach(i -> System.out.println(i));
	        Envelope env =  cityModel.getBoundedBy() != null ? cityModel.getBoundedBy().getEnvelope() : null;
						IPoint ptLC = null;
	        if (env != null) {
	        	List<Double> lcp =  env.getLowerCorner().getValue();
	 	        List<Double> ucp =  env.getUpperCorner().getValue();
				   ptLC = GamaPointFactory.create(lcp.get(0), lcp.get(1), lcp.get(2));
					envelope = GamaEnvelopeFactory.of(lcp.get(0), ucp.get(0), lcp.get(1), ucp.get(1), lcp.get(2), ucp.get(2));
	 	    }
							Map<String, Integer> cityObjects = new TreeMap<>();
						   IList<IShape> shapes = GamaListFactory.create();
	       
	       
	        for (AbstractCityObjectProperty cityObjectMember : cityModel.getCityObjectMembers()) {
	            AbstractCityObject cityObject = cityObjectMember.getObject();
	            
	            cityObjects.merge(cityObject.getClass().getSimpleName(), 1, Integer::sum);
	            cityObjects.forEach((key, value) -> System.out.println(key + ": " + value + " instance(s)"));
				
	            IShape gShape = null;
				
				for (Integer lod : cityObject.getGeometryInfo().getLods()) {
					List<GeometryProperty<?>> lodgeom = cityObject.getGeometryInfo().getGeometries(lod);
					IShape lodShape = BuildGeometriesGeom(scope, lodgeom);
					if (lodShape == null) continue;
					if (gShape == null) {
						gShape = lodShape;
					}
					gShape.getOrCreateAttributes().put("lod"+lod, lodShape.copy(scope));					
				}
				if (gShape != null) 
					shapes.add(gShape);
				
				
				if (cityObject instanceof AbstractSpace) {
					List<AbstractSpaceBoundaryProperty> boundaries = ((AbstractSpace)cityObject).getBoundaries();
					IShape shape = BuildGeometriesSpace(scope, boundaries);
					if (shape != null) {
																																														if (ptLC != null) shape.setLocation(shape.getLocation().minus(ptLC));
						shapes.add(shape);
					}
				}
				String sn = cityObject.getClass().getSimpleName();
	            switch (sn) {
				case "Building" -> { 
					Building b = (Building) cityObject;
					
					System.out.println(b.getId());
					
				}
				case "GenericOccupiedSpace" -> { 
					GenericOccupiedSpace b = (GenericOccupiedSpace) cityObject;
					System.out.println(b.getId());
					
				}
				default ->
					System.out.println("Unexpected city object: " + sn);
					//throw new IllegalArgumentException("Unexpected city object: " + sn);
				}
				System.out.println("shapes: " + shapes.size());
				setBuffer(shapes);

				// envelope will be computed on demand from the geometry
				envelope = null;
				 
	            
	            
	        }


		} catch (CityJSONContextException e) {
				e.printStackTrace();
		}

	}
	
	

}
