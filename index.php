<?php


#print "Esperar:" . $argv[1].;
    for($i = 0; ; $i++) {
        
        if($salva[$i] = $argv[1]){
            print "MD5:";
            print md5($argv[1]);
            $i++;
            break;
        } else {
            $salva[$i][md5($argv[1])];
            sleep($argv[1]);
            print "MD5:";
            print md5($argv[1]);
            $salva[$i] = $argv[1];
            $i++;
            break;
        }

    }