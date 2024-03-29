package husacct.analyse.task.analyser.java;

import java.util.List;
import org.antlr.runtime.tree.CommonTree;
import husacct.analyse.infrastructure.antlr.JavaParser;

class JavaImportGenerator extends JavaGenerator{

	public static int nodeType = JavaParser.IMPORT;
	
	private String importingClass;
	private String importedModule;
	private String completeImportDeclaration;
	private boolean isCompletePackageImport;
		
	public void generateFamixImport(CommonTree importTree, String className){
		this.importingClass = className;
		fillImportObject(importTree);
		modelService.createImport(importingClass, importedModule, completeImportDeclaration, isCompletePackageImport);
	}
	
	private void fillImportObject(CommonTree importTree){
		String importDetails = createImportDetails(importTree, "--");
		String declaration = convertToImportDeclaration(importDetails, "--");
		
		this.completeImportDeclaration = declaration;
		this.isCompletePackageImport = isPackageImport(declaration);
		if(isCompletePackageImport) importedModule = removeStar(declaration);
		else importedModule = declaration;
	}
	
	private String createImportDetails(CommonTree importTree, String detailSeperator){
		String details = "";
			@SuppressWarnings("unchecked")
			List<CommonTree> importDetail = (List<CommonTree>)importTree.getChildren();
			if(importDetail != null){
				if(importDetail.size() < 2){
					if(!isDot(importDetail.get(0))){
						details += importDetail.get(0).getText() + detailSeperator;
					}
					details += createImportDetails(importDetail.get(0), detailSeperator);
				}else{
					for(CommonTree singleDetail: importDetail){
						if(!isDot(singleDetail)){
							if(isStar(singleDetail)){
								details += "*" + detailSeperator;
							}
							else{
								details += singleDetail.getText() + detailSeperator;
							}
						}
						details += createImportDetails(singleDetail, detailSeperator);
					}
				}
			}	
		return details;
	}
	
	private String convertToImportDeclaration(String walkedImportDescription, String detailSeporator){
		String[] items = walkedImportDescription.split(detailSeporator);
		String declaration = "";
		for(int itemCount=0; itemCount < items.length; itemCount++){
			if(itemCount > 0){
				declaration += ".";
			}
			declaration += items[itemCount];
		}
		return declaration;
	}
	
	private boolean isPackageImport(String importString){
		return importString.contains("*");
	}
	
	private boolean isDot(CommonTree treeChild){
		return treeChild.getText().equals(".");
	}
	
	private boolean isStar(CommonTree treeChild){
		return treeChild.getText().equals(".*");
	}
	
	private String removeStar(String declaration){
		return declaration.replace(".*","");
	}
}
