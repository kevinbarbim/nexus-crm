# Modelo conceitual do Nexus

O DER apresenta as dez entidades do esquema atual na notação conceitual usada pelo brModelo. Retângulos representam entidades, losangos representam relacionamentos e círculos representam atributos. O círculo preenchido identifica a chave da entidade. As cardinalidades indicam mínimo e máximo: `(0,N)` permite nenhum ou vários registros associados; `(1,1)` exige exatamente um.

O DER único reúne as dez entidades do banco de dados, sem repetir entidades em desenhos separados. O arquivo `modelo/DER_Nexus.svg` é editável. `modelo/DER_Nexus.pdf` contém o desenho vetorial em uma página A4 vertical, permitindo ampliar e imprimir. `modelo/DER_Nexus.png` é a imagem usada no relatório.

O desenho contém usuários, leads, imóveis, interesses, atividades, visitas, propostas, ocorrências, histórico de etapas e tokens de recuperação, com doze relacionamentos conceituais.

Cada interesse associa um lead a um imóvel, com unicidade desse par. As visitas e propostas precisam corresponder a um interesse existente. O modelo conceitual representa esse vínculo pela entidade Interesse; as chaves estrangeiras compostas correspondentes estão no esquema SQL e no modelo lógico.

O responsável pelo atendimento e o usuário que atualiza o lead são papéis diferentes. O autor do histórico é o usuário registrado na alteração. Os tokens pertencem a um usuário.

O SVG permite editar o desenho e não é um arquivo nativo de projeto do brModelo. `DER.mmd` mantém o modelo lógico, e `Dicionario_de_Dados.md` e o Apêndice A do relatório descrevem todos os campos e as restrições. O DER conceitual mostra 35 atributos principais para conservar a legibilidade. `modelo/DER_Conceitual.json` descreve as entidades, os atributos exibidos e os relacionamentos do desenho.

