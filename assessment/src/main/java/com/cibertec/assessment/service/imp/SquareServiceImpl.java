package com.cibertec.assessment.service.imp;

import java.awt.Polygon;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.assessment.beans.PolygonBean;
import com.cibertec.assessment.model.Square;
import com.cibertec.assessment.repo.SquareRepo;
import com.cibertec.assessment.service.PolygonService;
import com.cibertec.assessment.service.SquareService;

@Service
public class SquareServiceImpl implements SquareService {

	@Autowired
	SquareRepo squareRepo;

	@Autowired
	PolygonService polygonService;

	// Al momento de crear se debe validar si
	// alguno de parte del cuadrado se encuentra dentro de algun
	// poligono y de ser asi se debe capturar el id de los poligonos y
	// guardar como un string pero con formato de array
	// Ejemplo polygons = "["1","2"]"
	// Se guardan los ids correspondites
	// usar los metodos ya existentes para listar polygonos
	@Override
	public Square create(Square s) {

		String polygons = recuperarIdPolygons(s);
		polygons = removerComma(polygons);
		polygons = "[" + polygons + "]";
		s.setPolygons(polygons);
		return squareRepo.save(s);
	}

	@Override
	public List<Square> list() {
		return squareRepo.findAll();
	}

	private String recuperarIdPolygons(Square objSquare) {

		String response = "";
		int[] intArrayXPoly = new int[5];
		int[] intArrayYPoly = new int[5];
		int[] intArrayXSqr = new int[4];
		int[] intArrayYSqr = new int[4];

		convertStringInIntegerArray(objSquare.getXPoints(), objSquare.getYPoints(), intArrayXSqr, intArrayYSqr);

		for (com.cibertec.assessment.model.Polygon row : polygonService.listAll()) {

			convertStringInIntegerArray(row.getXPoints(), row.getYPoints(), intArrayXPoly, intArrayYPoly);

			Polygon polygon = new Polygon(intArrayXPoly, intArrayYPoly, intArrayXPoly.length);
			Polygon square = new Polygon(intArrayXSqr, intArrayYSqr, intArrayXSqr.length);

			boolean isIntersecting = validarInterseccion(polygon, square);

			if (isIntersecting) {
				response = response + row.getId().toString() + ", ";
			}

		}

		return response;
	}

	private void convertStringInIntegerArray(String xPoints, String yPoints, int[] intArrayX, int[] intArrayY) {

		String cleanedXPoints = xPoints.substring(1, xPoints.length() - 1);
		String cleanedYPoints = yPoints.substring(1, yPoints.length() - 1);

		// Split the string by commas
		String[] partsX = cleanedXPoints.split(", ");
		String[] partsY = cleanedYPoints.split(", ");

		for (int i = 0; i < partsX.length; i++) {
			intArrayX[i] = Integer.parseInt(partsX[i]);
		}

		for (int i = 0; i < partsY.length; i++) {
			intArrayY[i] = Integer.parseInt(partsY[i]);
		}
	}

	private boolean validarInterseccion(Polygon polygon, Polygon square) {
		boolean response = false;
		for (int i = 0; i < square.npoints; i++) {
			if (polygon.contains(square.xpoints[i], square.ypoints[i])) {
				return true;
			}
		}
		return response;
	}

	public static String removerComma(String input) {
		if (input.endsWith(", ")) {
			return input.substring(0, input.length() - 2);
		} else {
			return input;
		}
	}
}
