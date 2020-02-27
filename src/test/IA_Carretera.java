package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class IA_Carretera {
    /*
    static class carrito{
        int velocidad=10;
        int calidad=10;
        int estado;
        //llantas
            //tipo
            //estado
            //peso
        
        int llantas=10;
        public void aleatorio(){
            this.calidad=(int)(Math.random()*100+1);
        }
        public void definir(){
            
        }
    }*/
    //String x=JOptionPane.showInputDialog("Seleccione el tipo de coche\n1-Default\n2-Aleatorio\n3-Definir");
    //int opcion=Integer.parseInt(x);
    /*
    Carrito
        0 velocidad
        1 calidad
        2 estado
        llantas
            3 tipo
            4 estado
            5 hora salida
            6 recorrido
            7 tiempo viaje
            8 sueño conductor
            9 cuenta con herramientas
            10 riesgo de accidente
            11 velocidad actual
    */
	
	//Mongo
	//static MongoClient mongo = new MongoClient("localhost", 27017);
	//static MongoDatabase db = mongo.getDatabase("caminos");
	//static DBCollection collection = db.getCollection("ejecuciones");
	//static MongoCollection<Document> col = db.getCollection("ejecuciones");
	static int seccionCon = 0;
	static int caminoCon = 1;
	static Connection conexion = new Conexion().obtener();
	
    static int corridos,
            lluvia,
            nieve,
            horas,
            contCorridos,
            contMorritas,
            contCerveza,
            contTrafico,
            fantasma=0,
            gasolineras=0,
            taller=0,
            grua=0,
            militares=0,
            morritas=0,
            cerveza=0,
            caseta=0,
            bache=0,
            huachicol=0,
            deslave=0,
            motel=0,
            dia,
            atajo=0;
	static float tempTerreno=0;
    static boolean flagLluvia,
            flagNieve,
            flagFantasma,
            flagMorritas,
            flagCerveza,
            flagTrafico,
            flagDeslave,
            flagCorridos,
            flagSenales,
            noche;
    static int caminos = 3;
    //static int resto=0;
    static boolean fin=false;
    static float[][] preAnalisis = new float[100][100];
    static float[] suma_total = new float[caminos];
    static List<Integer> talleres = new ArrayList<Integer>();
    static List<Integer> gruas = new ArrayList<Integer>();
    //static float[] talleres = new float[20];
    //static float[] gruas = new float[20];
    
    public static void main(String[] args) {
	
        talleres.add(0);
        gruas.add(0);
        float[][] carrito = new float[3][13];
        int contador = 0;
        int cont=1;
        int x1=(int)(Math.random()*100+51);
        int x2=(int)(Math.random()*100+1);
        int x3=(int)(Math.random()*100+1);
        int x4=(int)(Math.random()*3+1);
        int x5=(int)(Math.random()*100+1);
        int tiempo=(int)((Math.random()*24+1)*(60));
        //carrito carrito = new carrito();
        //int caminos = (int)(Math.random()*100+1);
        int secciones = (int)(Math.random()*100+1);
        for (float[] fs : carrito) {
            fs[0]=x1;
            fs[1]=x2;
            fs[2]=x3;
            fs[3]=x4;
            fs[4]=x5;
            fs[5]=tiempo;
            fs[6]=0;
            fs[7]=tiempo;
            fs[8]=0;
            fs[9]=(int)(Math.random()*2);
            fs[10]=0;
            fs[11]=x1;
            fs[12]=40;
            //carrito[5]=(int)(Math.random()*100+1);
            System.out.println("Coche "+cont);
            System.out.println("Hora de salida: "+((int)(fs[5]/60))+":"+((int)(fs[5]%60)));
            System.out.println("    Velocidad: "+fs[0]);
            System.out.println("    Calidad: "+fs[1]);
            System.out.println("    Estado: "+fs[2]);
            System.out.println("    Llantas: ");
            System.out.println("        Tipo: "+fs[3]);
            System.out.println("        Estado: "+fs[4]);
            String herramienta=fs[8]==(float)(1)? "Si":"No";
            System.out.println("Cuenta con herramienta: "+herramienta);
            cont++;
        }
        /////////////////////////////Importante///////////////////////////////////////////
        mejorCamino();
        int i=0;
        float[][][]  matriz = new float[caminos][secciones][6];
        for (float[][] fs : matriz) {
            System.out.println("-----------------------------------------------------");
            System.out.println("Carrito "+(contador+1)+":");
            for (float[] f : fs) {
                generar(f);
                eventos(f, carrito[i]);
                analizar(f, contador);
                //generarTiempo(f,carrito[i],i);
                seccionCon++;
            }
            contador++;
            if(fin==false) {
            	System.out.println("El auto llego a su destino");
                finalizar(carrito[i], "destino", 1);
            }
            fin=false;
            i++;
            caminoCon++;
            seccionCon=0;
        }
        cont=1;
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
        for (float[] fs : carrito) {
            System.out.println("Coche "+cont);
            System.out.println("Recorrido:"+fs[6]+" metros");
            System.out.println("Tiempo de viaje:"+((int)(fs[7]/60))+":"+(int)(fs[7]%60));
            System.out.println("Sueño de conductor:"+fs[8]);
            System.out.println("    Velocidad: "+fs[0]);
            System.out.println("    Calidad: "+fs[1]);
            System.out.println("    Estado: "+fs[2]);
            System.out.println("    Llantas: ");
            System.out.println("        Tipo: "+fs[3]);
            System.out.println("        Estado: "+fs[4]);
            String herramienta=fs[8]==(float)(1)? "Si":"No";
            System.out.println("Cuenta con herramienta: "+herramienta);
            System.out.println("Riesgo de accidente: "+fs[10]);
            System.out.println("Gasolina:"+fs[12]);
            cont++;
        }
        //imprimir(matriz);
    }
    private static void mejorCamino() {
    	try {
            Statement s = conexion.createStatement();
            for (int i = 1; i < 4; i++) {
                float promedio=0;
                for (int j = 0; j < 3; j++) {
                    try {
                        ResultSet rs = s.executeQuery ("select * from `caminos_de_la_vida`.`"+i+"` where seccion='"+j+"'");
                        int contador=0;
                        int contEvent1=0,contEvent2=0,contEvent3=0,contEvent4=0,contEvent5=0,contEvent6=0,contEvent7=0,contEvent8=0,contEvent9=0,
                            contEvent10=0,contEvent11=0,contEvent12=0,contEvent13=0,contEvent14=0,contEvent15=0,contEvent16=0,contEvent17=0,
                            contEvent18=0,contEvent19=0,contEvent20=0,contEvent21=0,contEvent22=0,contEvent23=0,contEvent24=0,contEvent25=0,contEvent26=0;
                        while(rs.next()){
                            String suceso=rs.getString("suceso");
                            switch(suceso){
                                case "muerte por choque":
                                    contEvent1++;
                                    break;
                                case "choque menor":
                                    contEvent2++;
                                    break;
                                case "reparacion":
                                    contEvent3++;
                                    break;
                                case "fin por auto descompuesto":
                                    contEvent4++;
                                    break;
                                case "corridos muy alterados":
                                    contEvent5++;
                                    break;
                                case "animal en el camino":
                                    contEvent6++;
                                    break;
                                case "falta de senalamientos":
                                    contEvent7++;
                                    break;
                                case "no trafico":
                                    contEvent8++;
                                    break;
                                case "lluvia":
                                    contEvent9++;
                                    break;
                                case "nieve":
                                    contEvent10++;
                                    break;
                                case "sueno":
                                    contEvent11++;
                                    break;
                                case "fantasma":
                                    contEvent12++;
                                    break;
                                case "llanta ponchada":
                                    contEvent13++;
                                    break;
                                case "sin gasolina":
                                    contEvent14++;
                                    break;
                                case "gasolinera":
                                    contEvent15++;
                                    break;
                                case "recargar gasolina":
                                    contEvent16++;
                                    break;
                                case "taller":
                                    contEvent17++;
                                    break;
                                case "reten militar?":
                                    contEvent18++;
                                    break;
                                case "secuestro":
                                    contEvent19++;
                                    break;
                                case "morritas":
                                    contEvent20++;
                                    break;
                                case "cerveza":
                                    contEvent21++;
                                    break;
                                case "caseta":
                                    contEvent22++;
                                    break;
                                case "huachicol":
                                    contEvent23++;
                                    break;
                                case "deslave":
                                    contEvent24++;
                                    break;
                                case "motel":
                                    contEvent25++;
                                    break;
                                case "oh yeah":
                                    contEvent26++;
                                    break;
                            }
                            
                            promedio=(promedio+rs.getInt("riesgo_accidente"));
                            contador++;
                        }
                        promedio=(promedio/contador);
                        preAnalisis[i][j]=promedio<100000? promedio : 0;
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            //for (int j = 1; j < 4; j++) {
                //System.out.println("Camino "+j+":");
                for (int i = 0; i < 100; i++) {
                    try {
                        if(preAnalisis[1][i]!=0 || preAnalisis[2][i]!=0 || preAnalisis[3][i]!=0){
                            System.out.println(i+":"+preAnalisis[1][i]+"    "+preAnalisis[2][i]+"    "+preAnalisis[3][i]);
                        }
                    } catch (Exception e) {

                    }
                }
                System.out.println("****************************");
            //}
    	}catch (Exception e) {
            System.out.println(e);
        }
		
	}
	public static void generar(float[] seccion){
        /*
        0   peso total
        1   complejidad
        2   tiempo
        3   longitud
        4   tipo
        5   estatus
        6 :data set next(array)
        */
        try{
            seccion[1]=(int) (Math.random()*100+1);
            seccion[3]=(int) (Math.random()*90000+1);
            seccion[4]=(int) ((Math.random()*4+1))*25;
            seccion[5]=(int) (Math.random()*100+1);
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public static void analizar(float[] seccion, int contador){
        try{
            seccion[0]=(seccion[1]+(100/3600*seccion[2]+(100/90000*seccion[3])+seccion[4]+seccion[5]));
            suma_total[contador]=(suma_total[contador]+seccion[0]);
        }catch(Exception e){
            
        }
    }
    public static void imprimir(float[][][] matriz){
        int cont_camino=0, cont_seccion=0, camino=0;
        float menor=1000000;
        for (float[][] fs : matriz) {
            System.out.println("Camino "+(cont_camino+1)+":");
            for (float[] f : fs) {
                if(f[2]!=0){
                    System.out.println("    Sección "+cont_seccion+":");
                    System.out.println("        Peso total: "+f[0]);
                    System.out.println("        Complejidad: "+f[1]);
                    System.out.println("        Tiempo: "+f[2]);
                    System.out.println("        Longitud: "+f[3]);
                    System.out.println("        Tipo: "+f[4]);
                    System.out.println("        Estado: "+f[5]);
                    cont_seccion++;
                }
            }
            System.out.println("Suma total: "+suma_total[cont_camino]);
            if(suma_total[cont_camino]<menor){
                menor=suma_total[cont_camino];
                camino=cont_camino;
            }
            cont_camino++;
            cont_seccion=0;
        }
        System.out.println("El mejor camino para escoger es : "+(camino+1));
    }
    public static void generarTiempo(float[] seccion, float[] carrito,int i) {
        /*
        0   peso total
        *1   complejidad
        2   tiempo
        *3   longitud
        *4   tipo
        *5   estado
        Carrito
        *0 velocidad
        1 calidad
        2 estado
        llantas
            3 tipo
            4 estado
            5 peso
        */
        
        //Condicion coche
        if(fin==false){
            if(carrito[2]>0&&carrito[4]>0){
                try {
                    double km=(double)(seccion[3]/1000);
                    float dano=0;
                    float estadoAuto=carrito[2];
                    int velocidad=(int)(carrito[0]);
                    //Tipo
                    switch((int)seccion[4]){
                        case 25:
                            switch((int)(carrito[3])){
                                case 1:
                                    dano=(float)(km*-.1);
                                    carrito[4]-=dano;
                                    break;
                                case 2:
                                    dano=(float)(km*-.1);
                                    carrito[4]-=dano;
                                    break;
                                case 3:
                                    dano=(float)(km*-.1);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.9);
                                    break;
                            }
                            carrito[2]-=(km*.1);
                            break;
                        case 50:
                            velocidad=(int)(velocidad*.95);
                            switch((int)(carrito[3])){
                                case 1:
                                    dano=(float)(km*-.3);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.95);
                                    break;
                                case 2:
                                    dano=(float)(km*-.2);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.95);
                                    break;
                                case 3:
                                    dano=(float)(km*-.2);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.92);
                                    break;
                            }
                            carrito[2]-=(km*.2);
                            break;
                        case 75:
                            velocidad=(int)(velocidad*.90);
                            switch((int)(carrito[3])){
                                case 1:
                                    dano=(float)(km*-.4);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.9);
                                    break;
                                case 2:
                                    dano=(float)(km*-.3);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.93);
                                    break;
                                case 3:
                                    dano=(float)(km*-.3);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.95);
                                    break;
                            }
                            carrito[2]-=(km*.35);
                            break;
                        case 100:
                            velocidad=(int)(velocidad*.85);
                            switch((int)(carrito[3])){
                                case 1:
                                    dano=(float)(km*-.5);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.85);
                                    break;
                                case 2:
                                    dano=(float)(km*-.3);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.9);
                                    break;
                                case 3:
                                    dano=(float)(km*-.1);
                                    carrito[4]-=dano;
                                    velocidad=(int)(velocidad*.95);
                                    break;
                            }
                            carrito[2]-=(km*.5);
                            break;
                    }
                    //dano coche estado
                    carrito[4]-=(km*((101-seccion[5])/200));
                    //Estado
                    velocidad=(int)(velocidad*(100-(seccion[5]/5))/100);
                    //complejidad
                    velocidad=(int)(velocidad*(100-(seccion[1]/6))/100);
                    //tiempo
                    seccion[2]=(seccion[3]/(velocidad*1000));

                } catch (Exception e) {
                    System.out.println("Algo paso cuate");
                }
            }else{
                System.out.println("Oh no, parece que el auto exploto en la seccion"+i);
                //carrito[6]=resto;
                fin=true;
            }
        }
    }
    static void eventos(float[] seccion, float[] carrito){
        try {
            if(fin==false){
                /*(tempTerreno!=0){
                    carrito[11]+=tempTerreno;
                    tempTerreno=0;
                }*/
            	tempTerreno=0;
                int refuerzo = 0;
                int tramo=(int)(carrito[6]+seccion[3]);
                //resto+=seccion[3];
                int x=5000;
                int resto=(tramo%1000);
                for (int i = (int)carrito[6]; i < tramo; i+=x) {
                    //Dia noche
                	if(carrito[7]>1440){
                        dia=(int)(carrito[7]/1440);
                    }
                	int y=(int) (carrito[7]%1440);
                	if(y>480 && y<1080){
                        noche=false;
                    }else{
                        noche=true;
                    }
                    ///////////Condiciones
                    int gasolin=(int)(carrito[6]/30000);
                    int talle=(int)(carrito[6]/50000);
                    int gru=(int)(carrito[6]/50000);
                    int militare=(int)(carrito[6]/100000);
                    int morrita=(int)(carrito[6]/90000);
                    int cervez=(int)(carrito[6]/75000);
                    int caset=(int)(carrito[6]/100000);
                    int bach=(int)(carrito[6]/20000);
                    int huachico=(int)(carrito[6]/30000);
                    int deslav=(int)(carrito[6]/50000);
                    int mote=(int)(carrito[6]/45000);
                    int ataj=(int)(carrito[6])/20000;
                    int fantasm=(int)(carrito[6]/80000);
                    ///////////////Eventos de seccion////////////////
                  //accidente
                    if(suceso((int)(carrito[10]))){
                        System.out.println("Veo la luz");
                        if(suceso(10)){
                            System.out.println("Adios mundo cruel");
                            refuerzo = 0;
                            guardar(seccion, carrito, "muerte por choque",refuerzo);
                            //carrito[6]=resto;
                            fin=true;
                            return;
                        }else{
                            System.out.println("Parece que no fue para tanto");
                            refuerzo = 0;
                            guardar(seccion, carrito, "choque menor",refuerzo);
                            if(salvacion((int)(carrito[6]),carrito)){
                                System.out.println("Encontre la forma de reparar mi auto");
                                refuerzo = 1;
                                guardar(seccion, carrito, "reparacion",refuerzo);
                            }else{
                                fin=true;
                                System.out.println("Aunque mi auto no lo logro");
                                refuerzo = 0;
                                guardar(seccion, carrito, "fin por auto descompuesto",refuerzo);
                            }
                        }
                    }
                    //Corridos alterados
                    if(flagCorridos && contCorridos<carrito[7]){
                        carrito[11]=(float) ((carrito[11]-(carrito[0]*.15)));
                        flagCorridos=false;
                    }
                    if(suceso(10) && flagCorridos==false){
                        refuerzo = 1;
                    	guardar(seccion, carrito, "corridos muy alterados",refuerzo);
                        System.out.println("Parece que alguien encontro una cancion del comander :)");
                        carrito[11]=(float) ((carrito[11])+((carrito[0]*.15)));
                        contCorridos=(int)carrito[7]+60;
                    }
                    //animal en el camino
                    if(suceso(6)){
                        refuerzo = 0;
                    	guardar(seccion, carrito, "animal en el camino",refuerzo);
                        System.out.println("Alguien paso por un tope de los que gritan");
                        carrito[2]=(float)(carrito[2]*1-((Math.random()*.5)+.5));
                    }
                    //señalamientos
                    if(flagSenales){
                        carrito[10]=carrito[10]-8;
                    }
                    if(suceso(15) && flagSenales==false){
                        refuerzo = 0;
                    	guardar(seccion, carrito, "falta de senalamientos",refuerzo);
                        System.out.println("No existen los suficientes se�alamientos");
                        carrito[10]=carrito[10]+8;
                    }
                    //no trafico
                    if(flagTrafico && carrito[6]>contTrafico){
                        contTrafico=(int)(carrito[6]+seccion[3]);
                        flagTrafico=false;
                    }
                    if(suceso(30) && flagTrafico==false){
                        refuerzo = 1;
                    	guardar(seccion, carrito, "no trafico",refuerzo);
                        System.out.println("Kuchau");
                        carrito[11]=(float)(carrito[11]+(carrito[0]*.15));
                        flagTrafico=true;
                    }
                    ///////////////Eventos de tiempo////////////////
                    //lluvia
                    if(carrito[7]>lluvia && flagLluvia==true){
                        carrito[11]=(float)(carrito[11]+(carrito[0]*.2));
                        carrito[10]=(carrito[10]-5);
                        flagLluvia=false;
                    }
                    if(suceso(10)){
                        refuerzo = 0;
                    	guardar(seccion, carrito, "lluvia",refuerzo);
                        System.out.println("Una pequeña lluvia");
                        lluvia=(int)(carrito[7]+60);
                        carrito[11]=(float)((carrito[11])-(carrito[0]*.2));
                        carrito[10]=(carrito[10]+5);
                        flagLluvia=true;
                    }
                    //nieve
                    if(carrito[6]>nieve && flagNieve==true){
                        carrito[11]=(float)((carrito[11])+(carrito[0]*.3));
                        carrito[10]=(carrito[10]-10);
                        flagNieve=false;
                    }
                    if(suceso(3)){
                        refuerzo = 0;
                    	guardar(seccion, carrito, "nieve",refuerzo);
                        System.out.println("¿Nieve... en junio?");
                        carrito[11]=(float)((carrito[11])-(carrito[0]*.3));
                        carrito[10]=(carrito[10]+10);
                        nieve=(int)(carrito[6]+10000);
                        flagNieve=true;
                    }
                    //sueño
                    if((carrito[7]-carrito[5])>180){
                        refuerzo = 0;
                    	guardar(seccion, carrito, "sueno",refuerzo);
                        System.out.println("Tengo algo de sueño");
                        horas=(int) ((carrito[7]-carrito[5])/60);
                        carrito[10]=(float)(carrito[10]+ (horas*2.5));
                        carrito[8]=(horas*5);
                    }
                    //fantasma
                    if(noche){
                        if(fantasm!=fantasma){
                                carrito[10]=(carrito[10]+75);
                                refuerzo = 0;
                                System.out.println("Hee Hee");
                                guardar(seccion, carrito, "fantasma",refuerzo);
                                flagFantasma=true;
                        }
                    }
                    if(flagFantasma && noche!=false){
                        System.out.println("Ay que miedo, yo mejor me voy, adios fantasma");
                        carrito[10]=(carrito[10]-75);
                        flagFantasma=false;
                    }
                    //desgaste de llantas
                    if(carrito[4]<5 || carrito[12]<=5){
                        if(carrito[9]==1){
                        	if(carrito[4]<5) {
                        		System.out.println("Parece que pisaste una tachuela :(");
                                        refuerzo = 0;
                                guardar(seccion, carrito, "llanta ponchada",refuerzo);
                                carrito[4]=100;
                                System.out.println("Llanta de remplazo:"+carrito[4]);
                        	}
                        	if(carrito[12]<=5) {
                        		System.out.println("Se acabo la gota :(");
                                        refuerzo = 0;
                                guardar(seccion, carrito, "sin gasolina",refuerzo);
                                carrito[12]=100;
                                System.out.println("Gasolina:"+carrito[4]);
                        	}
                            carrito[9]=0;
                        }else{
                            if(salvacion((int)carrito[6], carrito)){
                                System.out.println("Alguien vino a ayudarme");
                                refuerzo = 0;
                                guardar(seccion, carrito, "reparacion",refuerzo);
                            }else{
                                System.out.println("Nadie me quiere ayudar");
                                fin=true;
                            }
                        }
                    }
                    //Gasolinera
                    if(gasolin!=gasolineras){
                        if(suceso(50)){
                            System.out.println("Gasolinera en el camino");
                            refuerzo = 1;
                            guardar(seccion, carrito, "gasolinera",refuerzo);
                            if(carrito[12]<20){
                                System.out.println("10 pesos de la roja por favor");
                                guardar(seccion, carrito, "recargar gasolina",refuerzo);
                                carrito[12]=40;
                            }
                        }
                        gasolineras=gasolin;
                    }
                    //taller
                    if(talle!=taller){
                        if(suceso(50)){
                            refuerzo = 1;
                            System.out.println("Taller el ferras");
                            guardar(seccion, carrito, "taller",refuerzo);
                            talleres.add((int)carrito[6]);
                        }
                        taller=talle;
                    }
                    //militares
                    if(militares!=militare){
                        if(suceso(10)){
                            System.out.println("¿Porque esos militares tienen tenis?");
                            refuerzo = 0;
                            guardar(seccion, carrito, "reten militar?",refuerzo);
                            if(suceso(10)){
                                System.out.println("Hasta aqui llegaste morro X(");
                                guardar(seccion, carrito, "secuestro",refuerzo);
                                //carrito[6]=resto;
                                fin=true;
                                return;
                            }
                        }
                        militares=militare;
                    }
                    //morritas
                    if(flagMorritas && carrito[7]>contMorritas){
                        System.out.println("Adios mamis");
                        carrito[11]=(int)(carrito[11]+(carrito[0]*.15));
                        flagMorritas=false;
                    }
                    if(morritas!=morrita){
                        if(suceso(8)){
                            System.out.println("Hola mamis");
                            refuerzo = 0;
                            guardar(seccion, carrito, "morritas",refuerzo);
                            contMorritas=(int)(+60);
                            flagMorritas=true;
                            carrito[11]=(int)(carrito[11]-(carrito[0]*.15));
                        }
                        morritas=morrita;
                    }
                    //cerveza
                    if(flagCerveza && carrito[7]>contCerveza){
                        carrito[10]=(carrito[10]-15);
                        flagCerveza=false;
                    }
                    if(cerveza!=cervez){
                        System.out.println("Cerveza, cerveza");
                        refuerzo = 0;
                        guardar(seccion, carrito, "cerveza",refuerzo);
                        if(suceso(15)){
                            carrito[10]=(carrito[10]+15);
                            flagCerveza=true;
                            contCerveza=(int)(carrito[7]+60);
                        }
                        cerveza=cervez;
                    }
                    //caseta
                    if(caseta!=caset){
                        if(suceso(50)){
                            refuerzo = 0;
                            System.out.println("Crei que era una carretera libre");
                            guardar(seccion, carrito, "caseta",refuerzo);
                            carrito[7]=(carrito[7]+5);
                        }
                        caseta=caset;
                    }
                    //bache
                    if(bache!=bach){
                        if(suceso(30)){
                            refuerzo = 0;
                            System.out.println("Ese bache parecia la cueva del batman");
                            guardar(seccion, carrito, "bache",refuerzo);
                            carrito[4]=(float) (carrito[4]-.5);
                        }
                        bache=bach;
                    }
                    if(huachicol!=huachico){
                        if(suceso(10)){
                            refuerzo = 0;
                            System.out.println("Es robada//Gasolina");
                            guardar(seccion, carrito, "huachicol",refuerzo);
                            carrito[12]=40;
                            if(suceso(35)){
                                refuerzo = 0;
                            	guardar(seccion, carrito, "dano por huachicol",refuerzo);
                                carrito[2]=(carrito[2]-10);
                            }
                        }
                        huachicol=huachico;
                    }
                    //deslave
                    if(flagDeslave){
                        carrito[10]=(carrito[10]-50);
                        flagDeslave=false;
                    }
                    if(deslave!=deslav){
                        int prob=flagLluvia? 10 : 5;
                        if(suceso(prob)){
                            refuerzo = 0;
                            System.out.println("Oh no, un guijarro en el pavimento");
                            guardar(seccion, carrito, "deslave",refuerzo);
                            carrito[10]=(carrito[10]-50);
                            flagDeslave=true;
                        }
                        deslave=deslav;
                    }
                    
                    //motel
                    if(motel!=mote){
                        if(suceso(35)){
                            System.out.println("Motel red");
                            refuerzo = 1;
                            guardar(seccion, carrito, "motel",refuerzo);
                            if(flagMorritas=true){
                                System.out.println("Oh si, oh si");
                                guardar(seccion, carrito, "oh yeah",refuerzo);
                                carrito[7]=(carrito[7]+60);
                                flagMorritas=false;
                            }
                        }
                        motel=mote;
                    }
                    //Atajo
                    if(suceso(10)){
                        System.out.println("Mira un atajo");
                        carrito[7]=(carrito[7]-15);
                        atajo=ataj;
                    }
                    tempTerreno=0;
                    int vel=(int) carrito[11];
                    //Tipo
                    switch((int)seccion[4]){
                        case 25:
                            switch((int)(carrito[3])){
                                case 1:
                                    carrito[4]=(float) (carrito[4]-.1);
                                    break;
                                case 2:
                                	carrito[4]=(float) (carrito[4]-.1);
                                    break;
                                case 3:
                                	carrito[4]=(float) (carrito[4]-.1);
                                    tempTerreno=(float) (tempTerreno-(carrito[0]*.1));
                                    break;
                            }
                            break;
                        case 50:
                            tempTerreno-=(carrito[0]*.5);
                            switch((int)(carrito[3])){
                                case 1:
                                	carrito[4]=(float) (carrito[4]-.3);
                                	tempTerreno=(float) (tempTerreno-(carrito[0]*.05));
                                    break;
                                case 2:
                                	carrito[4]=(float) (carrito[4]-.2);
                                	tempTerreno=(float) (tempTerreno-(carrito[0]*.05));
                                    break;
                                case 3:
                                	carrito[4]=(float) (carrito[4]-.2);
                                	tempTerreno=(float) (tempTerreno-(carrito[0]*.08));
                                    break;
                            }
                            break;
                        case 75:
                            tempTerreno-=(carrito[0]*.1);
                            switch((int)(carrito[3])){
                                case 1:
                                	carrito[4]=(float) (carrito[4]-.4);
                                	tempTerreno=(float) (tempTerreno-(carrito[0]*.1));
                                    break;
                                case 2:
                                	carrito[4]=(float) (carrito[4]-.3);
                                	tempTerreno=(float) (tempTerreno-(carrito[0]*.07));
                                    break;
                                case 3:
                                	carrito[4]=(float) (carrito[4]-.3);
                                	tempTerreno=(float) (tempTerreno-(carrito[0]*.05));
                                    break;
                            }
                            break;
                        case 100:
                            tempTerreno-=(carrito[0]*.15);
                            switch((int)(carrito[3])){
                                case 1:;
                                carrito[4]=(float) (carrito[4]-.5);
                            	tempTerreno=(float) (tempTerreno-(carrito[0]*.15));
                                    break;
                                case 2:
                                	carrito[4]=(float) (carrito[4]-.3);
                                	tempTerreno=(float) (tempTerreno-(carrito[0]*.1));
                                    break;
                                case 3:
                                	carrito[4]=(float) (carrito[4]-.1);
                                	tempTerreno=(float) (tempTerreno-(carrito[0]*.05));
                                    break;
                            }
                            break;
                    }
                    carrito[11]=(carrito[11]-tempTerreno);
                    //tiempo
                    try {
                    float mts_min=0;
                    mts_min=((carrito[11]*1000)/60);
                    carrito[7]=(carrito[7]+(x/mts_min));
                    carrito[6]=(carrito[6]+x);
                    carrito[11]=vel;
                    carrito[12]=(carrito[12]-2);
                    }catch (Exception e) {
                    	System.out.println(e);
					}
                }
                carrito[6]=(carrito[6]+resto);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    static boolean suceso(int probabilidad){
        double x=(Math.random()*101);
        if(x<=probabilidad){
            return true;
        }else{
            return false;
        }
    }
    static boolean salvacion(int posicion, float[] carrito1){
        int Tmayor=posicion+1000;
        int Tmenor=posicion-1000;
        int Gmayor=posicion+5000;
        int Gmenor=posicion-5000;
        int Tcercano=100000;
        int Gcercano=100000;
        boolean flag=false;
        try{
            for (Integer t : talleres) {
                if(t<Tmayor && t>Tmenor){
                    if(t<Tcercano){
                        Tcercano=t;
                    }
                    flag=true;
                    carrito1[2]=100;
                    carrito1[4]=100;
                }
            }
            for (Integer g : gruas) {
                if(g<Gmayor && g>Gmenor){
                    if(g<Gcercano){
                        Gcercano=g;
                    }
                    flag=true;
                    carrito1[2]=100;
                    carrito1[4]=100;
                }
            }
            if(Tcercano!=100000 && Gcercano!=100000){
                int camino=Gcercano<Tcercano? Gcercano:Tcercano;
                
                carrito1[7]+=20;
                carrito1[6]=camino;
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return flag;
    }

    static void imprimirCoche(float[][] fs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //static class datos{
    	 /*
    	 private String velocidad_original,calidad_auto,estado_auto;
    	 private String llantas_tipo,llantas_estado;
    	 private String hora_salida,recorrido,tiempo_viaje,sueno,herramienta,riesgo_accidente,velocidad_actual;
    	 private String complejidad_seccion,tiempo_seccion,longitud_seccion,tipo_seccion,estatus_seccion;
    	 */

    static void guardar(float[] seccion, float[] carrito, String suceso,int refuerzo) {
            try {
                    PreparedStatement consulta;
                    consulta=conexion.prepareStatement("INSERT INTO `"+caminoCon+"` "
                                    + "(`suceso`, `seccion`, `velocidad_maxima`, "
                                    + "`calidad_auto`, `estado_auto`, `tipo_llantas`,"
                                    + "`estado_llantas`, `hora_salida`, `distancia_recorrida`,"
                                    + "`tiempo_viaje`, `sueno_conductor`, `herramientas`, "
                                    + "`riesgo_accidente`, `velocidad_actual`, `gasolina` , `refuerzo`) "
                                    + "VALUES ('"+suceso+"', '"+seccionCon+"', '"+((int)(carrito[0]))+"'"
                                    + ", '"+((int)(carrito[1]))+"', '"+((int)(carrito[2]))+"', '"+((int)(carrito[3]))+"', "
                                    + "'"+((int)(carrito[4]))+"', '"+((int)(carrito[5]))+"', '"+((int)(carrito[6]))+"',"
                                    + "'"+((int)(carrito[7]-carrito[5]))+"', '"+((int)(carrito[8]))+"', '"+((int)(carrito[9]))+"', "
                                    + "'"+((int)(carrito[10]))+"', '"+((int)(carrito[11]))+"', '"+((int)(carrito[12]))+"', '"+(refuerzo)+"');");
                    consulta.execute();


             }catch (Exception e) {
                    System.out.println(e);
             }
        //conexion.close();
     }

    private static void guardar(float[] seccion, float[] carrito, String muerte_por_choque) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void finalizar(float[] carrito, String suceso, int refuerzo) {
        try {
            PreparedStatement consulta;
            consulta=conexion.prepareStatement("INSERT INTO `"+caminoCon+"` "
                                    + "(`suceso`, `seccion`, `velocidad_maxima`, "
                                    + "`calidad_auto`, `estado_auto`, `tipo_llantas`,"
                                    + "`estado_llantas`, `hora_salida`, `distancia_recorrida`,"
                                    + "`tiempo_viaje`, `sueno_conductor`, `herramientas`, "
                                    + "`riesgo_accidente`, `velocidad_actual`, `gasolina` , `refuerzo`) "
                                    + "VALUES ('"+suceso+"', '"+seccionCon+"', '"+((int)(carrito[0]))+"'"
                                    + ", '"+((int)(carrito[1]))+"', '"+((int)(carrito[2]))+"', '"+((int)(carrito[3]))+"', "
                                    + "'"+((int)(carrito[4]))+"', '"+((int)(carrito[5]))+"', '"+((int)(carrito[6]))+"',"
                                    + "'"+((int)(carrito[7]-carrito[5]))+"', '"+((int)(carrito[8]))+"', '"+((int)(carrito[9]))+"', "
                                    + "'"+((int)(carrito[10]))+"', '"+((int)(carrito[11]))+"', '"+((int)(carrito[12]))+"', '"+(refuerzo)+"');");
                    consulta.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}