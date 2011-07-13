/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2009 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.components.map;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.component.BaseFillComponent;
import net.sf.jasperreports.engine.component.FillPrepareResult;
import net.sf.jasperreports.engine.fill.JRTemplateGenericElement;
import net.sf.jasperreports.engine.fill.JRTemplateGenericPrintElement;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: BarbecueFillComponent.java 4347 2011-05-26 11:13:03Z shertage $
 */
public class MapFillComponent extends BaseFillComponent
{
	private final MapComponent mapComponent;
	
	private Float latitude;
	private Float longitude;
	
	public MapFillComponent(MapComponent map)
	{
		this.mapComponent = map;
	}
	
	protected MapComponent getMap()
	{
		return mapComponent;
	}
	
	public void evaluate(byte evaluation) throws JRException
	{
		if (isEvaluateNow())
		{
			evaluateMap(evaluation);
		}
	}
	
	protected void evaluateMap(byte evaluation) throws JRException
	{
		latitude = (Float)fillContext.evaluate(mapComponent.getLatitudeExpression(), evaluation);
		longitude = (Float)fillContext.evaluate(mapComponent.getLongitudeExpression(), evaluation);
	}
	
	protected boolean isEvaluateNow()
	{
		return mapComponent.getEvaluationTime() == EvaluationTimeEnum.NOW;
	}

	public FillPrepareResult prepare(int availableHeight)
	{
		return isEvaluateNow() && (latitude == null || longitude == null) //FIXMEMAP check this null test 
				? FillPrepareResult.NO_PRINT_NO_OVERFLOW
				: FillPrepareResult.PRINT_NO_STRETCH;
	}

	public JRPrintElement fill()
	{
		JRComponentElement element = fillContext.getComponentElement();
		JRTemplateGenericElement template = new JRTemplateGenericElement(
				fillContext.getElementOrigin(), 
				fillContext.getDefaultStyleProvider(),
				MapPrintElement.MAP_ELEMENT_TYPE);
		
		JRTemplateGenericPrintElement printElement = new JRTemplateGenericPrintElement(template);
		printElement.setX(element.getX());
		printElement.setY(fillContext.getElementPrintY());
		printElement.setWidth(element.getWidth());
		printElement.setHeight(element.getHeight());

		if (isEvaluateNow())
		{
			copy(printElement);
		}
		else
		{
			fillContext.registerDelayedEvaluation(printElement, 
					mapComponent.getEvaluationTime(), mapComponent.getEvaluationGroup());
		}
		
		return printElement;
	}

	public void evaluateDelayedElement(JRPrintElement element, byte evaluation)
			throws JRException
	{
		evaluateMap(evaluation);
		copy((JRGenericPrintElement) element);
	}

	protected void copy(JRGenericPrintElement printElement)
	{
		printElement.setParameterValue(MapPrintElement.PARAMETER_LATITUDE, latitude);
		printElement.setParameterValue(MapPrintElement.PARAMETER_LONGITUDE, longitude);
	}
}