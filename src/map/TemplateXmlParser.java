package map;

import java.awt.geom.Point2D;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TemplateXmlParser  {
	
	private Entity templateParentEntity;
	private String absoluteTemplatePath;
	private boolean isCircular;
	private boolean isSquare;
	private boolean isObstruction;
	private float radius;
	private float length;
	private float width;
	private float orientation;
	private float obs_radius;
	private float obs_length;
	private float obs_width;
	private float obs_orientation;
	private Point2D.Float location;
	private MapXmlParser templateXmlParser;
	
	TemplateXmlParser(Entity entity)
	{
		templateParentEntity = entity;
		absoluteTemplatePath = entity.getPathFromTemplatePath();
		templateXmlParser = new MapXmlParser(absoluteTemplatePath);
		System.out.println(absoluteTemplatePath);
		NodeList entity_list = templateXmlParser.extractElementList("*");
		boolean templateContainsObstruction = false;
		String parent_template;

		if(entity_list.item(0) == null )
			return;

		for(int i = 0; i < entity_list.getLength(); i++)
		{
			if(entity_list.item(i).getNodeName().equals("Footprint"))
				parseFootprint((Element)entity_list.item(i));
			if(entity_list.item(i).getNodeName().equals("Obstruction")){
				templateContainsObstruction = true;
				parseObstruction((Element)entity_list.item(i));
			}
		}
		if(!templateContainsObstruction){
			String template = templateXmlParser.getRootElement().getAttribute("parent");
			String parent_template_path = new String("../0ad/0ad/binaries/data/mods/public/simulation/templates/" + template + ".xml");
			if(template.length() == 0)
				return;
			MapXmlParser parent_templateXmlParser = new MapXmlParser(parent_template_path);

			while(!parseTemplateParentForObstructions(parent_templateXmlParser))
			{
				template = parent_templateXmlParser.getRootElement().getAttribute("parent");
				parent_template_path = new String("../0ad/0ad/binaries/data/mods/public/simulation/templates/" + template + ".xml");
				if(template.length() == 0)
					return;
				parent_templateXmlParser = new MapXmlParser(parent_template_path);
			}
		}
	}
	
	private boolean parseTemplateParentForObstructions(MapXmlParser parent_template)
	{
		NodeList parent_entity_list = parent_template.getRootElement().getChildNodes();
		//NodeList parent_entity_list = parent_templateXmlParser.extractElementList("*");

		if(parent_entity_list.item(0) == null )
			return false;

		for(int i = 0; i < parent_entity_list.getLength(); i++)
		{
			if(parent_entity_list.item(i).getNodeName().equals("Footprint"))
				parseFootprint((Element)parent_entity_list.item(i));
			if(parent_entity_list.item(i).getNodeName().equals("Obstruction")){
				parseObstruction((Element)parent_entity_list.item(i));
				return true;
			}
		}
		
		return false;
	}
	private void parseObstruction(Element footprint){
		isObstruction = true;
		NodeList footprint_children = footprint.getChildNodes();
		
		if(footprint_children.item(1).getNodeName().equals("Unit"))
			parseUnitObstruction(footprint_children);
		else if(footprint_children.item(1).getNodeName().equals("Obstructions"))
			parseEdgeObstruction(footprint_children);
		else if(footprint_children.item(1).getNodeName().equals("Static"))
			parseStaticObstruction(footprint_children);
	}
	
	private void parseUnitObstruction(NodeList obstructionChildren){
		isCircular = true;
		obs_radius = templateXmlParser.getFloatAttribute("radius", (Element)obstructionChildren.item(1));
	}

	private void parseStaticObstruction(NodeList obstructionChildren){
		obs_length = templateXmlParser.getFloatAttribute("width", (Element)obstructionChildren.item(1));
		obs_width = templateXmlParser.getFloatAttribute("depth", (Element)obstructionChildren.item(1));
	}

	private void parseEdgeObstruction(NodeList obstructionChildren){
		isSquare = true;
		NodeList obstructionsChildren = obstructionChildren.item(1).getChildNodes();
		obs_length = templateXmlParser.getFloatAttribute("width", (Element)obstructionsChildren.item(1));
		obs_width = templateXmlParser.getFloatAttribute("depth", (Element)obstructionsChildren.item(1));
	}
		
	
	private void parseFootprint(Element footprint){
		NodeList footprint_children = footprint.getChildNodes();
		if(footprint_children.item(1).getNodeName().equals("Circle"))
			parseCircularFootprint(footprint_children);
		else
			parseSquareFootprint(footprint_children);
	}
	
	private void parseCircularFootprint(NodeList footPrintChildren){
			isCircular = true;
			radius = templateXmlParser.getFloatAttribute("radius", (Element)footPrintChildren.item(1));
	}
	
	private void parseSquareFootprint(NodeList footPrintChildren){
			isSquare = true;
			length = templateXmlParser.getFloatAttribute("width",(Element) footPrintChildren.item(1));
			width = templateXmlParser.getFloatAttribute("depth", (Element) footPrintChildren.item(1));
	}
	
	private void extractObstruction(){
		
	}
	
	public boolean isObstruction(){
		return isObstruction;
	}
	
	public boolean isCircular(){
		return isCircular;
	}

	public boolean isSquare(){
		return isSquare;
	}
	
	public float getObstructionRadius(){
		return obs_radius;
	}
	
	public float getObstructionLength(){
		return obs_length;
	}
	
	public float getObstructionWidth(){
		return obs_width;
	}
	
	public Point2D.Float getObstructionLocation(){
		return new Point2D.Float(templateParentEntity.getX(), templateParentEntity.getY());
	}

	public Float getObstructionOrientation(){
		return templateParentEntity.getTheta();
	}
	
	public String getTemplatePath()	{
		return absoluteTemplatePath;
	}
	
	public static void main(String args[]){
		
		//MapXmlParser map_element_entities = new MapXmlParser("../0ad/0ad/binaries/data/mods/public/maps/scenarios/Arcadia 02.xml");

		List obstructionList = new ArrayList();
		//EntityXmlParser entityParser = new EntityXmlParser("../0ad/0ad/binaries/data/mods/public/maps/scenarios/Arcadia 02.xml");
		//EntityXmlParser entityParser = new EntityXmlParser("../0ad/0ad/binaries/data/mods/public/maps/scenarios/Battle for the Tiber.xml");
		EntityXmlParser entityParser = new EntityXmlParser("../0ad/0ad/binaries/data/mods/public/maps/scenarios/Sandbox - Ptolemies.xml");
		List entityList = entityParser.getEntityList();
		Iterator listIterator = entityList.iterator();
		while(listIterator.hasNext()){
			TemplateXmlParser test = new TemplateXmlParser((Entity)listIterator.next());
			if(test.isObstruction())
				obstructionList.add(test);
		}
		
		System.out.println("number of obstructions" + obstructionList.size());
	}
	
}
