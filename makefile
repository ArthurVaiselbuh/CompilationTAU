###############
# DIRECTORIES #
###############
BASEDIR           = $(shell pwd)
JFlex_DIR         = ${BASEDIR}/FOLDER_0_JFlex
CUP_DIR           = ${BASEDIR}/FOLDER_1_CUP
SRC_DIR           = ${BASEDIR}/FOLDER_2_SRC
BIN_DIR           = ${BASEDIR}/FOLDER_3_BIN
INPUT_DIR         = ${BASEDIR}/FOLDER_4_INPUT
OUTPUT_DIR        = ${BASEDIR}/FOLDER_5_OUTPUT
EXTERNAL_JARS_DIR = ${BASEDIR}/FOLDER_7_EXTERNAL_JARS
MANIFEST_DIR      = ${BASEDIR}/FOLDER_8_MANIFEST

#########
# FILES #
#########
JFlex_GENERATED_FILE      = ${SRC_DIR}/Lexer.java
CUP_GENERATED_FILES       = ${SRC_DIR}/Parser.java ${SRC_DIR}/TokenNames.java
JFlex_CUP_GENERATED_FILES = ${JFlex_GENERATED_FILE} ${CUP_GENERATED_FILES}
SRC_FILES                 = ${SRC_DIR}/*.java              \
                            ${SRC_DIR}/IR/*.java           \
                            ${SRC_DIR}/AST/*.java          \
                            ${SRC_DIR}/TEMP/*.java         \
                            ${SRC_DIR}/MIPS/*.java         \
                            ${SRC_DIR}/TYPES/*.java        \
                            ${SRC_DIR}/SYMBOL_TABLE/*.java \
                            ${SRC_DIR}/Globals/*.java \
                            ${SRC_DIR}/RegisterAllocation/*.java
EXTERNAL_JAR_FILES        = ${EXTERNAL_JARS_DIR}/java-cup-11b-runtime.jar
MANIFEST_FILE             = ${MANIFEST_DIR}/MANIFEST.MF

########################
# DEFINITIONS :: JFlex #
########################
JFlex_PROGRAM  = jflex
JFlex_FLAGS    = -q
JFlex_DEST_DIR = ${SRC_DIR}
JFlex_FILE     = ${JFlex_DIR}/LEX_FILE.lex

######################
# DEFINITIONS :: CUP #
######################
CUP_PROGRAM                    = java -jar ${EXTERNAL_JARS_DIR}/java-cup-11b.jar 
CUP_FILE                       = ${CUP_DIR}/CUP_FILE.cup
CUP_GENERATED_PARSER_NAME      = Parser
CUP_GENERATED_SYMBOLS_FILENAME = TokenNames

######################
# DEFINITIONS :: CUP #
######################
CUP_FLAGS =                                \
-nowarn                                    \
-parser  ${CUP_GENERATED_PARSER_NAME}      \
-symbols ${CUP_GENERATED_SYMBOLS_FILENAME} 

#########################
# DEFINITIONS :: PARSER #
#########################
INPUT    = ${INPUT_DIR}/Input.txt
OUTPUT   = ${OUTPUT_DIR}/SemanticStatus.txt

##########
# TARGET #
##########
compile:
	clear
	@echo "*******************************"
	@echo "*                             *"
	@echo "*                             *"
	@echo "* [0] Remove COMPILER program *"
	@echo "*                             *"
	@echo "*                             *"
	@echo "*******************************"
	rm -rf COMPILER
	@echo "\n"
	@echo "************************************************************"
	@echo "*                                                          *"
	@echo "*                                                          *"
	@echo "* [1] Remove *.class files and JFlex-CUP generated files:  *"
	@echo "*                                                          *"
	@echo "*     Lexer.java                                           *"
	@echo "*     Parser.java                                          *"
	@echo "*     TokenNames.java                                      *"
	@echo "*                                                          *"
	@echo "************************************************************"
	rm -rf ${JFlex_CUP_GENERATED_FILES} ${BIN_DIR}/*.class ${BIN_DIR}/AST/*.class
	@echo "\n"
	@echo "************************************************************"
	@echo "*                                                          *"
	@echo "*                                                          *"
	@echo "* [2] Use JFlex to synthesize Lexer.java from LEX_FILE.lex *"
	@echo "*                                                          *"
	@echo "*                                                          *"
	@echo "************************************************************"
	$(JFlex_PROGRAM) ${JFlex_FLAGS} -d ${JFlex_DEST_DIR} ${JFlex_FILE}
	@echo "\n"
	@echo "*******************************************************************************"
	@echo "*                                                                             *"
	@echo "*                                                                             *"
	@echo "* [3] Use CUP to synthesize Parser.java and TokenNames.java from CUP_FILE.cup *"
	@echo "*                                                                             *"
	@echo "*                                                                             *"
	@echo "*******************************************************************************"
	$(CUP_PROGRAM) ${CUP_FLAGS} -destdir ${SRC_DIR} ${CUP_FILE}
	@echo "\n"
	@echo "********************************************************"
	@echo "*                                                      *"
	@echo "*                                                      *"
	@echo "* [4] Create *.class files from *.java files + CUP JAR *"
	@echo "*                                                      *"
	@echo "*                                                      *"
	@echo "********************************************************"
	javac -cp ${EXTERNAL_JAR_FILES} -d ${BIN_DIR} ${SRC_FILES}
	@echo "\n"
	@echo "***********************************************************"
	@echo "*                                                         *"
	@echo "*                                                         *"
	@echo "* [5] Create a JAR file from from *.class files + CUP JAR *"
	@echo "*                                                         *"
	@echo "*                                                         *"
	@echo "***********************************************************"
	jar cfm COMPILER ${MANIFEST_FILE} -C ${BIN_DIR} .
	
everything:
	clear
	@echo "******************************"
	@echo "*                            *"
	@echo "*                            *"
	@echo "* [0] Remove COMPIER program *"
	@echo "*                            *"
	@echo "*                            *"
	@echo "******************************"
	rm -rf COMPILER
	@echo "\n"
	@echo "************************************************************"
	@echo "*                                                          *"
	@echo "*                                                          *"
	@echo "* [1] Remove *.class files and JFlex-CUP generated files:  *"
	@echo "*                                                          *"
	@echo "*     Lexer.java                                           *"
	@echo "*     Parser.java                                          *"
	@echo "*     TokenNames.java                                      *"
	@echo "*                                                          *"
	@echo "************************************************************"
	rm -rf ${JFlex_CUP_GENERATED_FILES} ${BIN_DIR}/*.class ${BIN_DIR}/AST/*.class
	@echo "\n"
	@echo "************************************************************"
	@echo "*                                                          *"
	@echo "*                                                          *"
	@echo "* [2] Use JFlex to synthesize Lexer.java from LEX_FILE.lex *"
	@echo "*                                                          *"
	@echo "*                                                          *"
	@echo "************************************************************"
	$(JFlex_PROGRAM) ${JFlex_FLAGS} -d ${JFlex_DEST_DIR} ${JFlex_FILE}
	@echo "\n"
	@echo "*******************************************************************************"
	@echo "*                                                                             *"
	@echo "*                                                                             *"
	@echo "* [3] Use CUP to synthesize Parser.java and TokenNames.java from CUP_FILE.cup *"
	@echo "*                                                                             *"
	@echo "*                                                                             *"
	@echo "*******************************************************************************"
	$(CUP_PROGRAM) ${CUP_FLAGS} -destdir ${SRC_DIR} ${CUP_FILE}
	@echo "\n"
	@echo "********************************************************"
	@echo "*                                                      *"
	@echo "*                                                      *"
	@echo "* [4] Create *.class files from *.java files + CUP JAR *"
	@echo "*                                                      *"
	@echo "*                                                      *"
	@echo "********************************************************"
	javac -cp ${EXTERNAL_JAR_FILES} -d ${BIN_DIR} ${SRC_FILES}
	@echo "\n"
	@echo "***********************************************************"
	@echo "*                                                         *"
	@echo "*                                                         *"
	@echo "* [5] Create a JAR file from from *.class files + CUP JAR *"
	@echo "*                                                         *"
	@echo "*                                                         *"
	@echo "***********************************************************"
	jar cfm COMPILER ${MANIFEST_FILE} -C ${BIN_DIR} .
	@echo "\n"
	@echo "*****************************"
	@echo "*                           *"
	@echo "*                           *"
	@echo "* [6] Run resulting program *"
	@echo "*                           *"
	@echo "*                           *"
	@echo "*****************************"
	java -jar COMPILER ${INPUT} ${OUTPUT}
	@echo "\n"
	@echo "***************************************"
	@echo "*                                     *"
	@echo "*                                     *"
	@echo "* [7] Create a jpeg AST visualization *"
	@echo "*                                     *"
	@echo "*                                     *"
	@echo "***************************************"
	dot -Tjpeg -o${OUTPUT_DIR}/AST.jpeg ${OUTPUT_DIR}/AST_IN_GRAPHVIZ_DOT_FORMAT.txt
	@echo "\n"
	@echo "************************************************"
	@echo "*                                              *"
	@echo "*                                              *"
	@echo "* [8] Open the jpeg AST visualization with eog *"
	@echo "*                                              *"
	@echo "*                                              *"
	@echo "************************************************"
	#eog ${OUTPUT_DIR}/AST.jpeg &
	@echo "\n"
	@echo "***********************************************************"
	@echo "*                                                         *"
	@echo "*                                                         *"
	@echo "* [10] Allocate registers (just for this running example) *"
	@echo "*                                                         *"
	@echo "*                                                         *"
	@echo "***********************************************************"
	sed -i 's/\bTemp_0\b/$$t0/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_1\b/$$t1/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_2\b/$$t2/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_3\b/$$t3/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_4\b/$$t4/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_5\b/$$t5/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_6\b/$$t6/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_7\b/$$t7/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_8\b/$$t0/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_9\b/$$t1/g'  ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_10\b/$$t2/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_11\b/$$t3/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_12\b/$$t4/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_13\b/$$t5/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_14\b/$$t6/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_15\b/$$t7/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_16\b/$$t0/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_17\b/$$t1/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_18\b/$$t2/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_19\b/$$t3/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_20\b/$$t4/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_21\b/$$t5/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_22\b/$$t6/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_23\b/$$t7/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_24\b/$$t0/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_25\b/$$t1/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_26\b/$$t2/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_27\b/$$t3/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_28\b/$$t4/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_29\b/$$t5/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_30\b/$$t6/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_31\b/$$t7/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_32\b/$$t0/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_33\b/$$t1/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_34\b/$$t2/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_35\b/$$t3/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_36\b/$$t4/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_37\b/$$t5/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_38\b/$$t6/g' ${OUTPUT_DIR}/MIPS.txt
	sed -i 's/\bTemp_39\b/$$t7/g' ${OUTPUT_DIR}/MIPS.txt
	@echo "\n"
	@echo "*****************************************"
	@echo "*                                       *"
	@echo "*                                       *"
	@echo "* [11] Run spim and redirect its output *"
	@echo "*                                       *"
	@echo "*                                       *"
	@echo "*****************************************"
	spim -f ${OUTPUT_DIR}/MIPS.txt #> ${OUTPUT_DIR}/MIPS_OUTPUT.txt	
